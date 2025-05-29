package com.wtu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 医生注册数据传输对象
 */
@Data
@Schema(description = "医生注册请求参数")
public class RegisterDTO {
    
    /**
     * 登录用户名
     */
    @Schema(description = "登录用户名", example = "doctor_zhang", required = true)
    private String username;
    
    /**
     * 登录密码
     */
    @Schema(description = "登录密码", example = "123456", required = true)
    private String password;
    
    
    /**
     * 电子邮箱
     */
    @Schema(description = "电子邮箱", example = "doctor_zhang@example.com")
    private String email;
    
} 