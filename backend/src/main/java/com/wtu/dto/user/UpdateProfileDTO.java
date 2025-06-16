package com.wtu.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户个人信息更新DTO
 */
@Data
@Schema(description = "用户个人信息更新请求")
public class UpdateProfileDTO {
    
    /**
     * 真实姓名
     */
    @Schema(description = "医生真实姓名", example = "张医生")
    private String realName;
    
    /**
     * 科室
     */
    @Schema(description = "科室", example = "内科")
    private String department;
    
    /**
     * 职称
     */
    @Schema(description = "职称", example = "主任医师")
    private String title;
    
    /**
     * 电子邮箱
     */
    @Schema(description = "电子邮箱", example = "doctor_zhang@example.com")
    private String email;
    
    /**
     * 联系电话
     */
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;
} 