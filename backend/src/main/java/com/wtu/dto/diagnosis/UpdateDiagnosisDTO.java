package com.wtu.dto.diagnosis;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新诊断记录的请求DTO
 */
@Data
@Schema(description = "更新诊断记录请求")
public class UpdateDiagnosisDTO {

    /**
     * 最终诊断
     */
    @NotBlank(message = "最终诊断不能为空")
    @Schema(description = "最终诊断", required = true, example = "上呼吸道感染")
    private String finalDiagnosis;

    /**
     * 治疗方案
     */
    @Schema(description = "治疗方案", example = "建议服用布洛芬缓解症状，多喝水，充分休息...")
    private String treatmentPlan;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "COMPLETED", allowableValues = {"DRAFT", "ANALYZING", "PENDING", "COMPLETED"})
    private String status;
} 