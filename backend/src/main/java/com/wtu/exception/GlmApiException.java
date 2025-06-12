package com.wtu.exception;

/**
 * GLM API 调用相关的自定义异常
 */
public class GlmApiException extends RuntimeException {
    private int statusCode; // HTTP 状态码或 API 特定错误码

    /**
     * 默认构造函数，错误码为0
     * @param message 错误信息
     */
    public GlmApiException(String message) {
        super(message);
        this.statusCode = 0;
    }

    /**
     * 带原因的构造函数，错误码为0
     * @param message 错误信息
     * @param cause 原因
     */
    public GlmApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
    }

    /**
     * 带错误码的构造函数
     * 注意：此处的statusCode不是Result.code，而是HTTP状态码或API特定错误码
     * 在GlobalExceptionHandler中会将其转换为Result.code=0
     * @param statusCode HTTP状态码或API特定错误码
     * @param message 错误信息
     */
    public GlmApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * 带错误码和原因的构造函数
     * @param statusCode HTTP状态码或API特定错误码
     * @param message 错误信息
     * @param cause 原因
     */
    public GlmApiException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    /**
     * 获取状态码
     * @return 状态码
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * 设置状态码
     * @param statusCode 状态码
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}