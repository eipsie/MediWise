package com.wtu.constant;

/**
 * 诊断记录状态常量
 */
public class DiagnosisStatus {
    
    /**
     * 草稿状态 - 初始创建但未进行AI分析
     */
    public static final String DRAFT = "DRAFT";
    
    /**
     * 分析中 - AI正在分析中
     */
    public static final String ANALYZING = "ANALYZING";
    
    /**
     * 待确认 - AI分析完成，等待医生确认
     */
    public static final String PENDING = "PENDING";
    
    /**
     * 已完成 - 医生已确认诊断结果
     */
    public static final String COMPLETED = "COMPLETED";
    
    private DiagnosisStatus() {
        // 私有构造函数防止实例化
    }
} 