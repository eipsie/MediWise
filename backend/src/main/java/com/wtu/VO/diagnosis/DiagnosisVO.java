package com.wtu.VO.diagnosis;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 诊断记录视图对象
 */
@Data
@Schema(description = "诊断记录视图对象")
public class DiagnosisVO {
    
    /**
     * 诊断记录ID
     */
    @Schema(description = "诊断记录ID", example = "1")
    private Long id;
    
    /**
     * 患者ID
     */
    @Schema(description = "患者ID", example = "1")
    private Long patientId;
    
    /**
     * 患者姓名
     */
    @Schema(description = "患者姓名", example = "张三")
    private String patientName;
    
    /**
     * 医生ID
     */
    @Schema(description = "医生ID", example = "1")
    private Long doctorId;
    
    /**
     * 医生姓名
     */
    @Schema(description = "医生姓名", example = "李医生")
    private String doctorName;
    
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "诊断时间")
    private LocalDateTime diagnosisTime;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
} 