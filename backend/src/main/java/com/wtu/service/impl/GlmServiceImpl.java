package com.wtu.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wtu.VO.ai.GlmChatVO;
import com.wtu.config.GlmConfig;
import com.wtu.dto.ai.GlmChatDTO;
import com.wtu.exception.GlmApiException;
import com.wtu.service.GlmService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * GLM 聊天服务实现类
 */
@Service
@RequiredArgsConstructor
public class GlmServiceImpl implements GlmService {

    private static final Logger logger = LoggerFactory.getLogger(GlmServiceImpl.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(2);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(60);

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    private final GlmConfig glmConfig;

    private WebClient webClient;

    @PostConstruct
    private void init() {
        this.webClient = webClientBuilder
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + glmConfig.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public GlmChatVO chat(GlmChatDTO request) {
        // 设置默认值
        if (request.getStream() == null) {
            request.setStream(false);
        }

        logger.info("发送请求到GLM API，模型: {}, 温度: {}", request.getModel(), request.getTemperature());

        try {
            return callGlmApi(request);
        } catch (GlmApiException e) {
            // 直接向上抛出GlmApiException，由GlobalExceptionHandler处理
            throw e;
        } catch (Exception e) {
            // 将其他异常统一转换为GlmApiException
            logger.error("调用GLM API时发生未预期的错误", e);
            throw new GlmApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "GLM API调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 调用GLM API并处理响应
     * 
     * @param request 请求对象
     * @return GLM聊天响应
     */
    private GlmChatVO callGlmApi(GlmChatDTO request) {
        return webClient.post()
                .uri(glmConfig.getUrl())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parseResponse)
                .onErrorResume(WebClientResponseException.class, this::handleApiError)
                .retryWhen(createRetrySpec())
                .block(REQUEST_TIMEOUT);
    }

    /**
     * 创建重试策略
     * 
     * @return 重试规范
     */
    private Retry createRetrySpec() {
        return Retry.backoff(MAX_RETRY_ATTEMPTS, RETRY_DELAY)
                .filter(throwable -> {
                    // 只对服务器错误(5xx)进行重试
                    if (throwable instanceof GlmApiException) {
                        int statusCode = ((GlmApiException) throwable).getStatusCode();
                        return statusCode >= 500 && statusCode < 600;
                    }
                    return false;
                })
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                    Throwable failure = retrySignal.failure();
                    String message = "GLM API请求重试耗尽";
                    if (failure instanceof GlmApiException) {
                        return failure;
                    }
                    return new GlmApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                            message + ": " + failure.getMessage(), failure);
                });
    }

    /**
     * 解析API响应
     * 
     * @param responseBody 响应体
     * @return 解析后的响应对象
     */
    private Mono<GlmChatVO> parseResponse(String responseBody) {
        try {
            logger.debug("收到GLM API响应: {}", responseBody);
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // 检查错误响应
            if (rootNode.has("error") && rootNode.path("error").has("message")) {
                return handleErrorResponse(rootNode);
            }

            // 验证响应格式
            validateResponseFormat(rootNode, responseBody);

            // 提取响应数据
            String content = rootNode.path("choices").path(0).path("message").path("content").asText();
            String requestId = rootNode.path("id").asText("N/A");
            String model = rootNode.path("model").asText("N/A");
            Integer totalTokens = extractTotalTokens(rootNode);

            logger.info("成功解析GLM响应，内容长度: {}, token数: {}", content.length(), totalTokens);
            return Mono.just(new GlmChatVO(requestId, content, model, totalTokens));
        } catch (Exception e) {
            logger.error("解析GLM API响应失败", e);
            return Mono.error(new GlmApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "解析GLM API响应失败: " + e.getMessage(), e));
        }
    }

    /**
     * 处理错误响应
     * 
     * @param rootNode 响应JSON根节点
     * @return 错误Mono
     */
    private Mono<GlmChatVO> handleErrorResponse(JsonNode rootNode) {
        String errorMessage = rootNode.path("error").path("message").asText("未知的API错误");
        int errorCode = rootNode.path("error").path("code").asInt(HttpStatus.INTERNAL_SERVER_ERROR.value());
        logger.warn("GLM API返回错误: code={}, message={}", errorCode, errorMessage);
        return Mono.error(new GlmApiException(errorCode, errorMessage));
    }

    /**
     * 验证响应格式
     * 
     * @param rootNode 响应JSON根节点
     * @param responseBody 原始响应体
     */
    private void validateResponseFormat(JsonNode rootNode, String responseBody) {
        if (!rootNode.has("choices") || !rootNode.get("choices").isArray() || rootNode.get("choices").isEmpty()) {
            throw new GlmApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "无效的GLM API响应: 'choices'字段缺失或为空");
        }

        JsonNode firstChoice = rootNode.path("choices").path(0);
        if (!firstChoice.has("message") || !firstChoice.path("message").has("content")) {
            throw new GlmApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "无效的GLM API响应: 'message.content'字段缺失");
        }
    }

    /**
     * 提取总token数
     * 
     * @param rootNode 响应JSON根节点
     * @return token数量，如果不存在则返回null
     */
    private Integer extractTotalTokens(JsonNode rootNode) {
        JsonNode usageNode = rootNode.path("usage");
        return usageNode.has("total_tokens") ? usageNode.path("total_tokens").asInt() : null;
    }

    /**
     * 处理API错误
     * 
     * @param e WebClient响应异常
     * @return 错误Mono
     */
    private Mono<GlmChatVO> handleApiError(WebClientResponseException e) {
        String responseBody = e.getResponseBodyAsString();
        int statusCode = e.getStatusCode().value();
        
        logger.error("GLM API请求失败，状态码: {}, 响应: {}", statusCode, responseBody);
        
        try {
            // 尝试从响应中提取错误信息
            JsonNode errorNode = objectMapper.readTree(responseBody);
            String errorMessage = extractErrorMessage(errorNode, e.getMessage());
            return Mono.error(new GlmApiException(statusCode, errorMessage, e));
        } catch (Exception ex) {
            logger.error("解析GLM API错误响应失败", ex);
            return Mono.error(new GlmApiException(statusCode, 
                    "GLM API请求失败，无法解析错误响应: " + responseBody, e));
        }
    }

    /**
     * 从错误响应中提取错误信息
     * 
     * @param errorNode 错误JSON节点
     * @param defaultMessage 默认错误信息
     * @return 提取的错误信息
     */
    private String extractErrorMessage(JsonNode errorNode, String defaultMessage) {
        if (errorNode.has("error") && errorNode.path("error").has("message")) {
            return errorNode.path("error").path("message").asText(defaultMessage);
        } else if (errorNode.has("msg")) {
            return errorNode.path("msg").asText(defaultMessage);
        } else if (errorNode.has("message")) {
            return errorNode.path("message").asText(defaultMessage);
        }
        return defaultMessage;
    }
}
