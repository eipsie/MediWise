package com.wtu.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 密码修改DTO
 */
@Data
@Schema(description = "密码修改请求")
public class ChangePasswordDTO {
    
    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空")
    @Schema(description = "旧密码", example = "123456", required = true)
    private String oldPassword;
    
    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Schema(description = "新密码", example = "newpass123", required = true)
    private String newPassword;
    
    /**
     * 确认新密码
     */
    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认新密码", example = "newpass123", required = true)
    private String confirmPassword;
} 