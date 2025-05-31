package com.wtu.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlmChatVO {

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 响应内容
     */
    private String content;


    /**
     * 模型名称，如 "glm-4"
     */
    private String model;

    /**
     * 令牌使用情况
     */
    private Integer totalTokens;
}
