package com.wtu.dto.bloodtest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 血常规检查分析DTO
 */
@Data
@Schema(description = "血常规检查分析DTO")
public class BloodTestAnalyzeDTO {
    
    @NotNull(message = "血常规检查ID不能为空")
    @Schema(description = "血常规检查ID", example = "1", required = true)
    private Long bloodTestId;
    
    @Schema(description = "是否应用医学专业提示词", example = "true")
    private Boolean applyMedicalPrompt = true;
} 