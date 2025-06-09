package com.wtu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 诊断记录实体类
 * 对应数据库中的diagnosis_record表
 */
@Data
@TableName("diagnosis_record")
@Schema(description = "诊断记录实体")
public class DiagnosisRecord {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "诊断记录ID", example = "1")
    private Long id;
    
    /**
     * 患者ID
     */
    @Schema(description = "患者ID", example = "1")
    private Long patientId;
    
    /**
     * 医生ID
     */
    @Schema(description = "医生ID", example = "1")
    private Long doctorId;
    
    /**
     * 症状文本
     */
    @Schema(description = "症状文本", example = "患者主诉头痛三天，伴有发热...")
    private String symptomsText;
    
    /**
     * 结构化症状
     */
    @Schema(description = "结构化症状")
    private String symptomsStructured;
    
    /**
     * 生命体征
     */
    @Schema(description = "生命体征")
    private String vitalSigns;
    
    /**
     * 大模型请求数据
     */
    @Schema(description = "大模型请求数据")
    private String llmRequestData;
    
    /**
     * 大模型响应数据
     */
    @Schema(description = "大模型响应数据")
    private String llmResponseData;
    
    /**
     * 最终诊断
     */
    @Schema(description = "最终诊断", example = "上呼吸道感染")
    private String finalDiagnosis;
    
    /**
     * 治疗方案
     */
    @Schema(description = "治疗方案", example = "建议口服XXX，每日三次...")
    private String treatmentPlan;
    
    /**
     * 状态
     */
    @Schema(description = "状态", example = "COMPLETED")
    private String status;
    
    /**
     * 诊断时间
     */
    @Schema(description = "诊断时间")
    private LocalDateTime diagnosisTime;
    
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