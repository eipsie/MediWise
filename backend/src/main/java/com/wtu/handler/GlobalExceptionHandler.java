package com.wtu.handler;

import com.wtu.exception.GlmApiException;
import com.wtu.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 使用 @RestControllerAdvice 捕获 Controller 层抛出的异常并统一处理。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义的 GlmApiException。
     * @param e GlmApiException 实例
     * @return ResponseEntity 包装的 Result 对象
     */
    @ExceptionHandler(GlmApiException.class)
    public ResponseEntity<Result<Object>> handleGlmApiException(GlmApiException e) {
        logger.error("GLM API 调用失败 (GlmApiException): 状态码={}, 错误信息={}", e.getStatusCode(), e.getMessage(), e);
        // 根据 GlmApiException 的 statusCode 决定返回给客户端的 HTTP 状态
        // 通常，如果 statusCode 是 4xx 系列，可能对应 BAD_REQUEST 或其他客户端错误
        // 如果是 5xx 系列，则对应 INTERNAL_SERVER_ERROR
        // 为简化，这里可以设定一个通用规则或根据 statusCode 范围判断
        HttpStatus statusToReturn = HttpStatus.INTERNAL_SERVER_ERROR; // 默认
        if (e.getStatusCode() >= 400 && e.getStatusCode() < 500) {
            statusToReturn = HttpStatus.BAD_REQUEST; // 例如，API密钥错误等返回4xx
        }
        // 如果 GlmApiException statusCode 就是 HTTP 状态码，可以直接使用
        // HttpStatus resolvedStatus = HttpStatus.resolve(e.getStatusCode());
        // if (resolvedStatus != null) {
        //    statusToReturn = resolvedStatus;
        // }

        return ResponseEntity.status(statusToReturn)
                .body(Result.error("GLM服务调用时发生错误: " + e.getMessage()));
    }

    /**
     * 处理参数校验相关的 IllegalArgumentException。
     * @param e IllegalArgumentException 实例
     * @return ResponseEntity 包装的 Result 对象，HTTP 状态为 400 Bad Request。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("请求参数无效 (IllegalArgumentException): {}", e.getMessage(), e); // 可以选择不打印完整堆栈 e
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.error("请求参数错误: " + e.getMessage()));
    }

    /**
     * 处理所有其他未捕获的运行时异常。
     * @param e Exception 实例
     * @return ResponseEntity 包装的 Result 对象
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Object>> handleGenericException(Exception e) {
        logger.error("处理请求时发生未知错误 (Exception): {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error("服务器内部错误，请稍后重试: " + e.getMessage()));
    }
}

