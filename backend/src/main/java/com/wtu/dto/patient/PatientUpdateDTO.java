package com.wtu.dto.patient;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 患者更新DTO
 */
@Data
@Schema(description = "患者更新请求")
public class PatientUpdateDTO {
    
    /**
     * 患者ID
     */
    @Schema(description = "患者ID", example = "1", required = true)
    private Long id;
    
    /**
     * 姓名
     */
    @Schema(description = "姓名", example = "张三", required = true)
    private String name;
    
    /**
     * 性别(0:女,1:男,2:未知)
     */
    @Schema(description = "性别(0:女,1:男,2:未知)", example = "1", required = true)
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
} 