package com.wtu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wtu.entity.SystemLog;

import java.time.LocalDate;
import java.util.List;

public interface SystemLogService {
    /**
     * 保存日志
     */
    boolean saveLog(SystemLog systemLog);

    /**
     * 分页查询日志
     */
    Page<SystemLog> getLogsByPage(int page, int size, String logType, String actionType, String username, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取最近的日志
     */
    List<SystemLog> getRecentLogs(int limit);
    
    /**
     * 获取指定用户的日志
     */
    List<SystemLog> getUserLogs(Long userId, int limit);
}
