package com.wtu.VO.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "GLM聊天响应")
public class GlmChatVO {

    /**
     * 请求ID
     */
    @Schema(description = "请求ID", example = "req_123456")
    private String requestId;

    /**
     * 响应内容
     */
    @Schema(description = "AI响应内容")
    private String content;

    /**
     * 模型名称，如 "glm-4"
     */
    @Schema(description = "使用的模型名称", example = "glm-4")
    private String model;

    /**
     * 令牌使用情况
     */
    @Schema(description = "使用的token数量", example = "256")
    private Integer totalTokens;
} 