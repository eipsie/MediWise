package com.wtu.dto.bloodtest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 创建血常规检查DTO
 */
@Data
@Schema(description = "创建血常规检查DTO")
public class CreateBloodTestDTO {
    
    @NotNull(message = "患者ID不能为空")
    @Schema(description = "患者ID", example = "1", required = true)
    private Long patientId;
    
    @NotNull(message = "检测日期不能为空")
    @Schema(description = "检测日期", required = true)
    private LocalDateTime testDate;
    
    @Schema(description = "白细胞计数", example = "8.5")
    private BigDecimal wbc;
    
    @Schema(description = "红细胞计数", example = "4.8")
    private BigDecimal rbc;
    
    @Schema(description = "血红蛋白", example = "140")
    private BigDecimal hgb;
    
    @Schema(description = "血小板计数", example = "250")
    private BigDecimal plt;
    
    @Schema(description = "中性粒细胞百分比", example = "65.0")
    private BigDecimal neutp;
    
    @Schema(description = "淋巴细胞百分比", example = "30.5")
    private BigDecimal lymp;
} 