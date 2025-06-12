package com.wtu.dto.knowledge;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建医学知识库DTO
 */
@Data
@Schema(description = "创建医学知识库DTO")
public class CreateMedicalKnowledgeDTO {
    
    @NotBlank(message = "条目类型不能为空")
    @Schema(description = "条目类型(DISEASE/DRUG)", example = "DISEASE", required = true)
    private String entryType;
    
    @NotBlank(message = "标题不能为空")
    @Schema(description = "标题", example = "高血压概述", required = true)
    private String title;
    
    @Schema(description = "疾病名称", example = "高血压")
    private String diseaseName;
    
    @Schema(description = "疾病编码", example = "I10")
    private String diseaseCode;
    
    @Schema(description = "药品名称", example = "硝苯地平")
    private String drugName;
    
    @NotBlank(message = "分类不能为空")
    @Schema(description = "分类", example = "心血管疾病", required = true)
    private String category;
    
    @NotBlank(message = "描述不能为空")
    @Schema(description = "描述", example = "高血压是以体循环动脉血压（收缩压和/或舒张压）增高为主要特征的慢性疾病...", required = true)
    private String description;
    
    @Schema(description = "相关症状")
    private String symptoms;
    
    @Schema(description = "治疗指南", example = "生活方式干预与药物治疗相结合...")
    private String treatmentGuide;
    
    @NotNull(message = "状态不能为空")
    @Schema(description = "状态(1:启用,0:禁用)", example = "1", required = true)
    private Integer status;
} 