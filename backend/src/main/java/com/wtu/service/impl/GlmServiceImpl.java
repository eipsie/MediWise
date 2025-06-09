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

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    private final GlmConfig glmConfig;

    private WebClient webClient; // 不能是 final，因为它在 PostConstruct 中初始化

    @PostConstruct
    private void init() {
        this.webClient = webClientBuilder
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + glmConfig.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public GlmChatVO chat(GlmChatDTO request) {
        if (request.getStream() == null) {
            request.setStream(false);
        }

        logger.info("发送到 GLM API 的请求模型: {}, 温度: {}", request.getModel(), request.getTemperature());

        try {
            return webClient.post()
                    .uri(glmConfig.getUrl())
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(this::parseSuccessResponse)
                    .onErrorResume(WebClientResponseException.class, this::handleApiError)
                    .onErrorResume(Exception.class, e -> {
                        logger.error("处理 GLM 聊天请求时发生意外错误: {}", e.getMessage(), e);
                        return Mono.error(new GlmApiException(500, "处理 GLM 聊天请求时发生意外错误: " + e.getMessage(), e));
                    })
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                            .filter(throwable -> throwable instanceof GlmApiException && ((GlmApiException) throwable).getStatusCode() >= 500)
                            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                    new GlmApiException(500, "GLM API 请求在多次重试后失败: " + retrySignal.failure().getMessage(), retrySignal.failure())
                            )
                    )
                    .block(Duration.ofSeconds(60));
        } catch (GlmApiException e) {
            throw e; // 由 GlobalExceptionHandler 处理
        } catch (Exception e) {
            logger.error("GLM 聊天请求同步执行时发生错误: {}", e.getMessage(), e);
            throw new GlmApiException(500, "GLM 聊天请求失败: " + e.getMessage(), e); // 转换为 GlmApiException 或直接抛出，由 GlobalExceptionHandler 处理
        }
    }

    private Mono<GlmChatVO> parseSuccessResponse(String responseBody) {
        try {
            logger.debug("GLM API 原始响应: {}", responseBody);
            JsonNode rootNode = objectMapper.readTree(responseBody);

            if (rootNode.has("error") && rootNode.path("error").has("message")) {
                String errorMessage = rootNode.path("error").path("message").asText("未知的API错误");
                int errorCode = rootNode.path("error").path("code").asInt(500);
                logger.warn("GLM API 在成功响应中返回错误信息: code={}, message={}", errorCode, errorMessage);
                return Mono.error(new GlmApiException(errorCode, "API 返回错误: " + errorMessage));
            }

            if (!rootNode.has("choices") || !rootNode.get("choices").isArray() || rootNode.get("choices").isEmpty()) {
                logger.error("无效的 GLM API 响应: 'choices' 字段缺失或为空。响应体: {}", responseBody);
                return Mono.error(new GlmApiException(500, "无效的 GLM API 响应: 'choices' 字段缺失或为空。"));
            }

            JsonNode firstChoice = rootNode.path("choices").path(0);
            if (!firstChoice.has("message") || !firstChoice.path("message").has("content")) {
                logger.error("无效的 GLM API 响应: 'message.content' 字段缺失。响应体: {}", responseBody);
                return Mono.error(new GlmApiException(500, "无效的 GLM API 响应: 'message.content' 字段缺失。"));
            }

            String content = firstChoice.path("message").path("content").asText();
            String requestId = rootNode.path("id").asText("N/A");
            String model = rootNode.path("model").asText("N/A");

            JsonNode usageNode = rootNode.path("usage");
            Integer totalTokens = usageNode.has("total_tokens") ? usageNode.path("total_tokens").asInt() : null;

            logger.info("响应内容: {}, token {}", content, totalTokens);
            return Mono.just(new GlmChatVO(requestId, content, model, totalTokens));

        } catch (Exception e) {
            logger.error("解析 GLM API 成功响应失败: {}. 响应体: {}", e.getMessage(), responseBody, e);
            return Mono.error(new GlmApiException(500, "解析 GLM API 响应失败: " + e.getMessage(), e));
        }
    }

    private Mono<GlmChatVO> handleApiError(WebClientResponseException e) {
        String responseBody = e.getResponseBodyAsString();
        logger.error("GLM API 请求失败，状态码: {}, 消息: {}. 响应体: {}",
                e.getStatusCode().value(), e.getMessage(), responseBody);
        try {
            JsonNode errorNode = objectMapper.readTree(responseBody);
            String errorMessage = "未知的 API 错误";
            if (errorNode.has("error") && errorNode.path("error").has("message")) {
                errorMessage = errorNode.path("error").path("message").asText(e.getMessage());
            } else if (errorNode.has("msg")) {
                errorMessage = errorNode.path("msg").asText(e.getMessage());
            } else if (errorNode.has("message")) {
                errorMessage = errorNode.path("message").asText(e.getMessage());
            } else {
                errorMessage = responseBody;
            }
            return Mono.error(new GlmApiException(e.getStatusCode().value(), errorMessage, e));
        } catch (Exception ex) {
            logger.error("解析 GLM API 错误响应失败: {}", ex.getMessage(), ex);
            return Mono.error(new GlmApiException(e.getStatusCode().value(), "解析 API 错误响应失败: " + responseBody, ex));
        }
    }
}
