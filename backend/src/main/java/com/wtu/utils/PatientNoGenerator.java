package com.wtu.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 患者编号生成器
 * 格式：P + 年份 + 5位序号，如P20250001
 */
public class PatientNoGenerator {
    
    private static final String PREFIX = "P";
    private static final DateTimeFormatter YEAR_FORMAT = DateTimeFormatter.ofPattern("yyyy");
    private static final AtomicInteger SEQUENCE = new AtomicInteger(1);
    private static String currentYear = null;
    
    /**
     * 生成患者编号
     * @return 患者编号
     */
    public synchronized static String generatePatientNo() {
        String year = LocalDate.now().format(YEAR_FORMAT);
        
        // 如果年份变了，重置序号
        if (currentYear == null || !currentYear.equals(year)) {
            currentYear = year;
            SEQUENCE.set(1);
        }
        
        // 获取序号并格式化为5位数
        int sequence = SEQUENCE.getAndIncrement();
        String formattedSequence = String.format("%05d", sequence);
        
        return PREFIX + year + formattedSequence;
    }
    
    /**
     * 设置当前序号（用于系统初始化，从数据库中已有的最大序号开始）
     * @param maxSequence 当前最大序号
     */
    public static void setCurrentSequence(int maxSequence) {
        SEQUENCE.set(maxSequence + 1);
    }
} 