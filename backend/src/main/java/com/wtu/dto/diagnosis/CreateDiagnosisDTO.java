package com.wtu.dto.diagnosis;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建诊断记录的请求DTO
 */
@Data
@Schema(description = "创建诊断记录请求")
public class CreateDiagnosisDTO {

    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    @Schema(description = "患者ID", required = true, example = "1")
    private Long patientId;

    /**
     * 症状文本
     */
    @NotNull(message = "症状描述不能为空")
    @Schema(description = "症状文本描述", required = true, example = "患者主诉头痛三天，伴有发热38.5℃，全身酸痛...")
    private String symptomsText;

    /**
     * 结构化症状（JSON格式）
     */
    @Schema(description = "结构化症状（JSON格式）", example = "{\"头痛\":\"严重\",\"发热\":\"38.5℃\"}")
    private String symptomsStructured;

    /**
     * 生命体征（JSON格式）
     */
    @Schema(description = "生命体征（JSON格式）", example = "{\"体温\":\"38.5\",\"血压\":\"120/80\",\"心率\":\"85\"}")
    private String vitalSigns;
} 