package com.wtu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者实体类
 * 对应数据库中的patient表
 */
@Data
@TableName("patient")
@Schema(description = "患者实体")
public class Patient {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "患者ID", example = "1")
    private Long id;
    
    /**
     * 患者编号
     */
    @Schema(description = "患者编号", example = "P20250001")
    private String patientNo;
    
    /**
     * 姓名
     */
    @Schema(description = "姓名", example = "张三")
    private String name;
    
    /**
     * 性别(0:女,1:男,2:未知)
     */
    @Schema(description = "性别(0:女,1:男,2:未知)", example = "1")
    private Integer gender;
    
    /**
     * 出生日期
     */
    @Schema(description = "出生日期", example = "1990-01-01")
    private LocalDate birthDate;
    
    /**
     * 身份证号
     */
    @Schema(description = "身份证号", example = "110101199001011234")
    private String idCard;
    
    /**
     * 电话
     */
    @Schema(description = "电话", example = "13800138000")
    private String phone;
    
    /**
     * 地址
     */
    @Schema(description = "地址", example = "北京市海淀区")
    private String address;
    
    /**
     * 过敏史
     */
    @Schema(description = "过敏史", example = "青霉素过敏")
    private String allergies;
    
    /**
     * 既往病史
     */
    @Schema(description = "既往病史", example = "高血压、糖尿病史")
    private String medicalHistory;
    
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
    
    /**
     * 创建医生ID
     */
    @Schema(description = "创建医生ID", example = "1")
    private Long creatorId;
} 