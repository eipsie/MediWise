package com.wtu.service;


import com.wtu.VO.ai.GlmChatVO;
import com.wtu.dto.ai.GlmChatDTO;

public interface GlmService {
    /**
     * 发送聊天请求到 GLM 模型。
     *
     * @param request 包含聊天请求参数的 DTO 对象。
     * @return 包含模型响应的 VO 对象。
     * @throws com.wtu.exception.GlmApiException 如果 API 调用失败或处理过程中发生错误。
     */
    GlmChatVO chat(GlmChatDTO request);
}
