package com.wtu.dto.diagnosis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 诊断分析请求DTO
 */
@Data
@Schema(description = "诊断分析请求")
public class DiagnosisAnalyzeDTO {
    
    /**
     * 诊断记录ID - 分析已存在的诊断记录
     */
    @Schema(description = "诊断记录ID", example = "1")
    private Long diagnosisId;
    
    /**
     * 是否应用专业医学提示词
     */
    @Schema(description = "是否应用专业医学提示词", example = "true", defaultValue = "true")
    private Boolean applyMedicalPrompt = true;
} 