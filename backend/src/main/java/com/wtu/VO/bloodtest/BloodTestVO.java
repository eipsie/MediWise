package com.wtu.VO.bloodtest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 血常规检查VO
 */
@Data
@Schema(description = "血常规检查VO")
public class BloodTestVO {
    
    @Schema(description = "血常规ID", example = "1")
    private Long id;
    
    @Schema(description = "患者ID", example = "1")
    private Long patientId;
    
    @Schema(description = "患者姓名", example = "张三")
    private String patientName;
    
    @Schema(description = "检测日期")
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
    
    @Schema(description = "AI分析结果")
    private String aiAnalysisResult;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
} 