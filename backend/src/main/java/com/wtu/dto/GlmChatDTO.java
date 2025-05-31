package com.wtu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlmChatDTO {

    /**
     * 模型名称，例如："glm-4"
     * 这个字段至关重要，用于指定使用哪个 GLM 模型。
     */
    private String model;

    /**
     * 构成对话历史的消息列表。
     * GLM API 通常要求消息按特定顺序和格式排列。
     */
    private List<Message> messages;

    /**
     * 控制模型输出的随机性。值越小，输出越趋向于确定性。
     * 取值范围通常在 0.0 到 1.0 之间 (具体范围请参考模型文档，例如 GLM 为 (0.0, 1.0]，设为0则表现为0.01)。
     * 如果不传递该参数，模型将使用默认值 (例如 GLM 默认为 0.95)。
     */
    private Float temperature;

    /**
     * 是否启用流式输出。对于同步调用，此值通常为 false。
     * 可选字段：如果不需要或有默认值，可以移除。
     */
    private Boolean stream; // 如果客户端未显式设置，则默认为 false


    /**
     * 表示对话中的单条消息。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        /**
         * 消息发送者的角色，例如："user" (用户), "assistant" (助手), "system" (系统)。
         */
        private String role;

        /**
         * 消息的内容。
         */
        private String content;
    }

    /**
     * 用于简单用户提示的便捷构造函数。
     * @param model 要使用的模型。
     * @param userPrompt 用户的输入。
     */
    public GlmChatDTO(String model, String userPrompt) {
        this.model = model;
        this.messages = List.of(new Message("user", userPrompt));
        this.stream = false; // 非流式输出的默认值
        this.temperature = 0.95f; // 为 temperature 设置一个常用的默认值
    }

}
