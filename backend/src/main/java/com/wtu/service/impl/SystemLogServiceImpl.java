package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wtu.entity.SystemLog;
import com.wtu.mapper.SystemLogMapper;
import com.wtu.service.SystemLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Service
public class SystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SystemLog> implements SystemLogService {

    /**
     * 保存日志
     *
     * @param systemLog 系统日志对象
     * @return 是否保存成功
     */
    @Override
    public boolean saveLog(SystemLog systemLog) {
        if(systemLog.getLogTime() == null){
            systemLog.setLogTime(LocalDateTime.now());
        }
        systemLog.setCreateTime(LocalDateTime.now());
        return save(systemLog);
    }

    /**
     * 分页查询日志
     *
     * @param page 页码
     * @param size 每页大小
     * @param logType 日志类型
     * @param actionType 操作类型
     * @param username 用户名
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分页结果
     */
    @Override
    public Page<SystemLog> getLogsByPage(int page, int size, String logType, String actionType, String username, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<SystemLog> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        if(StringUtils.hasText(logType)){
            queryWrapper.eq(SystemLog::getLogType, logType);
        }
        if(StringUtils.hasText(actionType)){
            queryWrapper.eq(SystemLog::getActionType, actionType);
        }
        if(StringUtils.hasText(username)){
            queryWrapper.eq(SystemLog::getUsername, username);
        }
        
        // 日期范围查询
        if(startDate != null && endDate != null) {
            LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
            queryWrapper.between(SystemLog::getLogTime, startDateTime, endDateTime);
        } else if(startDate != null) {
            LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
            queryWrapper.ge(SystemLog::getLogTime, startDateTime);
        } else if(endDate != null) {
            LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
            queryWrapper.le(SystemLog::getLogTime, endDateTime);
        }

        // 按时间倒序
        queryWrapper.orderByDesc(SystemLog::getLogTime);

        return page(new Page<>(page, size), queryWrapper);
    }
    
    /**
     * 获取最近的日志
     *
     * @param limit 限制数量
     * @return 最近的操作日志列表
     */
    @Override
    public List<SystemLog> getRecentLogs(int limit) {
        LambdaQueryWrapper<SystemLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(SystemLog::getLogTime);
        queryWrapper.last("LIMIT " + limit);
        
        return list(queryWrapper);
    }
    
    /**
     * 获取指定用户的日志
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 用户的操作日志列表
     */
    @Override
    public List<SystemLog> getUserLogs(Long userId, int limit) {
        LambdaQueryWrapper<SystemLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemLog::getUserId, userId);
        queryWrapper.orderByDesc(SystemLog::getLogTime);
        queryWrapper.last("LIMIT " + limit);
        
        return list(queryWrapper);
    }
}
