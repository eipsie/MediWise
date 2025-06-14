package com.wtu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 患者数据验证异常
 * 当患者数据不符合业务规则时抛出此异常
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PatientValidationException extends BusinessException {
    
    public PatientValidationException(String message) {
        super(message);
    }
    
    public PatientValidationException(String message, Integer code) {
        super(message, code);
    }
    
    public PatientValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public PatientValidationException(String message, Integer code, Throwable cause) {
        super(message, code, cause);
    }
}
