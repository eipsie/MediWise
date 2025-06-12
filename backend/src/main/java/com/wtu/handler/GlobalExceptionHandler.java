package com.wtu.handler;

import com.wtu.exception.BusinessException;
import com.wtu.exception.GlmApiException;
import com.wtu.exception.ResourceNotFoundException;
import com.wtu.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 全局异常处理器
 * 使用 @RestControllerAdvice 捕获 Controller 层抛出的异常并统一处理。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     * @param e 业务异常
     * @return ResponseEntity 包装的 Result 对象
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Object>> handleBusinessException(BusinessException e) {
        logger.error("业务异常: {}", e.getMessage());
        Result<Object> result = Result.error(e.getMessage());
        // 如果业务异常的code不是0，则设置为自定义code，否则保持默认的0
        if (e.getCode() != null && e.getCode() != 0) {
            result.setCode(e.getCode());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
    
    /**
     * 处理资源不存在异常
     * @param e 资源不存在异常
     * @return ResponseEntity 包装的 Result 对象
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Result<Object>> handleResourceNotFoundException(ResourceNotFoundException e) {
        logger.error("资源不存在异常: {}", e.getMessage());
        Result<Object> result = Result.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }
    
    /**
     * 处理数据完整性异常（如数据库约束违反）
     * @param e 数据完整性异常
     * @return ResponseEntity 包装的 Result 对象
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Result<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        logger.error("数据完整性异常: {}", e.getMessage());
        String message = e.getMessage();
        
        // 处理JSON格式错误
        if (message.contains("Invalid JSON text")) {
            message = "JSON格式错误：请确保JSON字段格式正确";
        } 
        // 处理唯一约束错误
        else if (message.contains("Duplicate entry")) {
            message = "数据已存在：请检查是否有重复数据";
        }
        // 处理外键约束错误
        else if (message.contains("foreign key constraint")) {
            message = "关联数据错误：请检查关联数据是否存在";
        }
        // 其他数据库错误
        else {
            message = "数据错误：" + message;
        }
        
        Result<Object> result = Result.error(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
    
    /**
     * 处理自定义的 GlmApiException
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

        // 创建错误响应，确保code为0表示错误
        Result<Object> result = Result.error("GLM服务调用时发生错误: " + e.getMessage());
        
        return ResponseEntity.status(statusToReturn).body(result);
    }

    /**
     * 处理参数校验异常
     * @param e 参数校验异常
     * @return ResponseEntity 包装的 Result 对象
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Object>> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder errorMsg = new StringBuilder("参数校验失败: ");
        
        for (FieldError fieldError : fieldErrors) {
            errorMsg.append(fieldError.getField())
                    .append(":")
                    .append(fieldError.getDefaultMessage())
                    .append(", ");
        }
        
        String message = errorMsg.substring(0, errorMsg.length() - 2);
        logger.error(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.error(message));
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
     * 处理Spring Security权限不足异常
     * @param e AccessDeniedException 实例
     * @return ResponseEntity 包装的 Result 对象，HTTP 状态为 403 Forbidden。
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Object>> handleAccessDeniedException(AccessDeniedException e) {
        logger.warn("权限不足 (AccessDeniedException): {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.error("权限不足，无法执行此操作"));
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

