package com.wtu.service;

/**
 * 智谱AI GLM大模型聊天服务接口
 */
public interface GlmChatService {
    
    /**
     * 发送聊天请求到GLM大模型
     *
     * @param prompt 提示词
     * @return 模型响应文本
     */
    String chat(String prompt);
} 