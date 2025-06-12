package com.wtu.exception;

/**
 * 业务异常类
 * 用于表示业务逻辑中的错误，会被全局异常处理器捕获并返回给前端
 */
public class BusinessException extends RuntimeException {
    
    private Integer code;
    
    /**
     * 默认构造函数，错误码为0
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 0;
    }
    
    /**
     * 带错误码的构造函数
     * @param message 错误信息
     * @param code 错误码
     */
    public BusinessException(String message, Integer code) {
        super(message);
        this.code = code;
    }
    
    /**
     * 获取错误码
     * @return 错误码
     */
    public Integer getCode() {
        return code;
    }
} 