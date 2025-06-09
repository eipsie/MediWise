package com.wtu.VO.patient;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者视图对象
 */
@Data
@Schema(description = "患者信息")
public class PatientVO {
    
    /**
     * 患者ID
     */
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
     * 性别文本
     */
    @Schema(description = "性别文本", example = "男")
    private String genderText;
    
    /**
     * 出生日期
     */
    @Schema(description = "出生日期", example = "1990-01-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    
    /**
     * 年龄
     */
    @Schema(description = "年龄", example = "33")
    private Integer age;
    
    /**
     * 身份证号
     */
    @Schema(description = "身份证号", example = "11010119900101****")
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    
    /**
     * 创建医生ID
     */
    @Schema(description = "创建医生ID", example = "1")
    private Long creatorId;
    
    /**
     * 创建医生姓名
     */
    @Schema(description = "创建医生姓名", example = "李医生")
    private String creatorName;
} 