package com.wtu.dto.knowledge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新医学知识库DTO
 */
@Data
@Schema(description = "更新医学知识库DTO")
public class UpdateMedicalKnowledgeDTO {
    
    @Schema(description = "标题", example = "高血压概述")
    private String title;
    
    @Schema(description = "疾病名称", example = "高血压")
    private String diseaseName;
    
    @Schema(description = "疾病编码", example = "I10")
    private String diseaseCode;
    
    @Schema(description = "药品名称", example = "硝苯地平")
    private String drugName;
    
    @Schema(description = "分类", example = "心血管疾病")
    private String category;
    
    @Schema(description = "描述", example = "高血压是以体循环动脉血压（收缩压和/或舒张压）增高为主要特征的慢性疾病...")
    private String description;
    
    @Schema(description = "相关症状")
    private String symptoms;
    
    @Schema(description = "治疗指南", example = "生活方式干预与药物治疗相结合...")
    private String treatmentGuide;
    
    @Schema(description = "状态(1:启用,0:禁用)", example = "1")
    private Integer status;
} 