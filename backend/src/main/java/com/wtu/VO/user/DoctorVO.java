package com.wtu.VO.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 医生信息视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "医生信息视图对象")
public class DoctorVO {
    
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
}
