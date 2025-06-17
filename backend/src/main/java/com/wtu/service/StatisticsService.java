package com.wtu.service;

import java.util.Map;

/**
 * 统计数据服务接口
 */
public interface StatisticsService {
    
    /**
     * 获取管理员控制台统计数据
     * @return 统计数据Map
     */
    Map<String, Object> getAdminDashboardStatistics();
    
    /**
     * 获取医生控制台统计数据
     * @return 统计数据Map
     */
    Map<String, Object> getDoctorDashboardStatistics();
} 