package com.wtu.controller;

import com.wtu.VO.GlmChatVO;
import com.wtu.dto.GlmChatDTO;
import com.wtu.result.Result;
import com.wtu.service.GlmService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "GLM聊天接口", description = "提供与智谱GLM大模型交互的API")
@RequestMapping("/api")
@RequiredArgsConstructor
public class GlmChatController {
    private static final Logger logger = LoggerFactory.getLogger(GlmChatController.class);

    private final GlmService glmService;


    /**
     * 处理聊天请求。
     * 接收包含模型名称、消息历史等的请求体，并返回模型的响应，使用 Result<T>进行包装。
     * 异常将由全局异常处理器捕获。
     *
     * @param chatRequest 包含聊天请求参数的 DTO 对象。
     * @return Result<GlmChatVO> 对象，成功时 HTTP 状态码默认为 200 OK。
     */
    @PostMapping("/chat")
    public Result<GlmChatVO> chatWithGlm(@RequestBody GlmChatDTO chatRequest) {
        // 基本的请求校验
        if (chatRequest.getMessages() == null || chatRequest.getMessages().isEmpty()) {
            logger.warn("接收到的聊天请求消息列表为空或null");
            // 抛出异常，由 GlobalExceptionHandler 处理并返回 400 状态码
            throw new IllegalArgumentException("请求消息不能为空");
        }
        logger.info("接收到聊天请求，最后一条消息内容: {}", chatRequest.getMessages().get(chatRequest.getMessages().size()-1).getContent());

        GlmChatVO glmChatVO = glmService.chat(chatRequest);

        logger.info("成功返回 GLM 响应, 请求ID: {}", glmChatVO.getRequestId());
        // 直接返回 Result 对象，Spring MVC 会自动处理序列化和 HTTP 200 OK 状态码
        return Result.success(glmChatVO);
    }
}
