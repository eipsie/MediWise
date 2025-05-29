package com.wtu.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 医生注册返回视图对象
 */
@Data
@Schema(description = "医生注册返回视图对象")
public class RegisterVO {
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
     * 医生真实姓名
     */
    @Schema(description = "医生真实姓名", example = "张医生")
    private String realName;
    
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