package com.wtu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 血常规检查实体类
 * 对应数据库中的blood_test表
 */
@Data
@TableName("blood_test")
@Schema(description = "血常规检查实体")
public class BloodTest {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "血常规ID", example = "1")
    private Long id;
    
    /**
     * 患者ID
     */
    @Schema(description = "患者ID", example = "1")
    private Long patientId;
    
    /**
     * 检测日期
     */
    @Schema(description = "检测日期")
    private LocalDateTime testDate;
    
    /**
     * 白细胞计数
     */
    @Schema(description = "白细胞计数", example = "8.5")
    private BigDecimal wbc;
    
    /**
     * 红细胞计数
     */
    @Schema(description = "红细胞计数", example = "4.8")
    private BigDecimal rbc;
    
    /**
     * 血红蛋白
     */
    @Schema(description = "血红蛋白", example = "140")
    private BigDecimal hgb;
    
    /**
     * 血小板计数
     */
    @Schema(description = "血小板计数", example = "250")
    private BigDecimal plt;
    
    /**
     * 中性粒细胞百分比
     */
    @Schema(description = "中性粒细胞百分比", example = "65.0")
    private BigDecimal neutP;
    
    /**
     * 淋巴细胞百分比
     */
    @Schema(description = "淋巴细胞百分比", example = "30.5")
    private BigDecimal lymP;
    
    /**
     * AI分析结果
     */
    @Schema(description = "AI分析结果")
    private String aiAnalysisResult;
    
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