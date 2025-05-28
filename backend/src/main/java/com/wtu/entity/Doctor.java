package com.wtu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 医生实体类
 * 对应数据库中的doctor表
 */
@Data
@TableName("doctor")
@Schema(description = "医生实体")
public class Doctor {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "医生ID", example = "1")
    private Long id;
    
    /**
     * 登录用户名
     */
    @Schema(description = "登录用户名", example = "doctor_zhang")
    private String username;
    
    /**
     * 登录密码(MD5加密)
     */
    @Schema(description = "登录密码(MD5加密)", example = "e10adc3949ba59abbe56e057f20f883e")
    private String password;
    
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