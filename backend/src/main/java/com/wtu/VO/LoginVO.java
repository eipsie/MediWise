package com.wtu.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录返回视图对象
 */
@Data
@Schema(description = "登录返回视图对象")
public class LoginVO {
    /**
     * 医生ID
     */
    @Schema(description = "医生ID", example = "1")
    private Long id;

    /**
     * 登录用户名
     */
    @Schema(description = "登录用户名", example = "doctor_zhang")
    private String username;

    /**
     * 登录令牌
     */
    @Schema(description = "登录令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    /**
     * 医生真实姓名
     */
    @Schema(description = "医生真实姓名", example = "张医生")
    private String realName;
    
    /**
     * 角色
     */
    @Schema(description = "角色(DOCTOR/ADMIN)", example = "DOCTOR")
    private String role;
    
    /**
     * 科室
     */
    @Schema(description = "科室", example = "内科")
    private String department;
    
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
    
    /**
     * 状态(1:正常,0:禁用)
     */
    @Schema(description = "状态(1:正常,0:禁用)", example = "1")
    private Integer status;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
