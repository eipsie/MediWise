package com.wtu.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wtu.service.GlmChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 智谱AI GLM大模型聊天服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GlmChatServiceImpl implements GlmChatService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${glm.api.url:https://open.bigmodel.cn/api/paas/v4/chat/completions}")
    private String apiUrl;

    @Value("${glm.api.key:}")
    private String apiKey;

    @Value("${glm.api.model:glm-4}")
    private String model;

    @Override
    public String chat(String prompt) {
        try {
            // 准备请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 准备请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            
            requestBody.put("messages", new Object[]{message});
            requestBody.put("temperature", 0.7);
            requestBody.put("top_p", 0.8);
            requestBody.put("max_tokens", 2000);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);
            String responseBody = response.getBody();

            // 解析响应
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode choicesNode = rootNode.path("choices");
            
            if (choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode messageNode = choicesNode.get(0).path("message");
                if (messageNode.has("content")) {
                    return messageNode.path("content").asText();
                }
            }

            log.warn("无法从GLM响应中提取内容: {}", responseBody);
            return "无法获取AI响应，请稍后再试";
        } catch (Exception e) {
            log.error("调用GLM API失败", e);
            throw new RuntimeException("调用GLM API失败: " + e.getMessage());
        }
    }
} 