package com.wtu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 医学知识库实体类
 * 对应数据库中的medical_knowledge表
 */
@Data
@TableName("medical_knowledge")
@Schema(description = "医学知识库实体")
public class MedicalKnowledge {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "知识库ID", example = "1")
    private Long id;
    
    /**
     * 条目类型
     */
    @Schema(description = "条目类型(DISEASE/DRUG)", example = "DISEASE")
    private String entryType;
    
    /**
     * 标题
     */
    @Schema(description = "标题", example = "高血压概述")
    private String title;
    
    /**
     * 疾病名称
     */
    @Schema(description = "疾病名称", example = "高血压")
    private String diseaseName;
    
    /**
     * 疾病编码
     */
    @Schema(description = "疾病编码", example = "I10")
    private String diseaseCode;
    
    /**
     * 药品名称
     */
    @Schema(description = "药品名称", example = "硝苯地平")
    private String drugName;
    
    /**
     * 分类
     */
    @Schema(description = "分类", example = "心血管疾病")
    private String category;
    
    /**
     * 描述
     */
    @Schema(description = "描述", example = "高血压是以体循环动脉血压（收缩压和/或舒张压）增高为主要特征的慢性疾病...")
    private String description;
    
    /**
     * 相关症状
     */
    @Schema(description = "相关症状")
    private String symptoms;
    
    /**
     * 治疗指南
     */
    @Schema(description = "治疗指南", example = "生活方式干预与药物治疗相结合...")
    private String treatmentGuide;
    
    /**
     * 状态(1:启用,0:禁用)
     */
    @Schema(description = "状态(1:启用,0:禁用)", example = "1")
    private Integer status;
    
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