package com.wtu.exception;

/**
 * GLM API 调用相关的自定义异常
 */
public class GlmApiException extends RuntimeException {
    private int statusCode; // HTTP 状态码或 API 特定错误码

    public GlmApiException(String message) {
        super(message);
    }

    public GlmApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public GlmApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public GlmApiException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}