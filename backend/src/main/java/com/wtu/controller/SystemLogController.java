package com.wtu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wtu.VO.log.SystemLogVO;
import com.wtu.annotation.AdminOnly;
import com.wtu.annotation.OperationLog;
import com.wtu.entity.SystemLog;
import com.wtu.result.Result;
import com.wtu.service.SystemLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/system/logs")
@RequiredArgsConstructor
@Tag(name = "系统日志", description = "系统日志接口")
public class SystemLogController {

    private final SystemLogService systemLogService;

    @GetMapping
    @AdminOnly
    @Operation(summary = "分页查询系统日志", description = "管理员功能：分页查询系统日志")
    @OperationLog(actionType = "LOG_QUERY", actionDesc = "查询系统日志")
    public Result<Page<SystemLogVO>> getLogs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "日志类型") @RequestParam(required = false) String logType,
            @Parameter(description = "操作类型") @RequestParam(required = false) String actionType,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        Page<SystemLog> logPage = systemLogService.getLogsByPage(page, size, logType, actionType, username, startDate, endDate);

        // 转换为VO
        List<SystemLogVO> records = logPage.getRecords().stream().map(log -> {
            SystemLogVO vo = new SystemLogVO();
            BeanUtils.copyProperties(log, vo);
            return vo;
        }).collect(Collectors.toList());

        Page<SystemLogVO> voPage = new Page<>();
        voPage.setRecords(records);
        voPage.setTotal(logPage.getTotal());
        voPage.setCurrent(logPage.getCurrent());
        voPage.setSize(logPage.getSize());

        return Result.success(voPage);
    }
    
    @GetMapping("/recent")
    @Operation(summary = "获取最近的日志活动", description = "获取系统中最近的操作日志")
    public Result<List<SystemLogVO>> getRecentActivities(
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "5") int limit) {
        List<SystemLog> logs = systemLogService.getRecentLogs(limit);
        
        List<SystemLogVO> voList = logs.stream().map(log -> {
            SystemLogVO vo = new SystemLogVO();
            BeanUtils.copyProperties(log, vo);
            return vo;
        }).collect(Collectors.toList());
        
        return Result.success(voList);
    }
    
    @GetMapping("/user")
    @Operation(summary = "获取特定用户的操作日志", description = "获取指定用户的操作日志记录")
    public Result<List<SystemLogVO>> getUserLogs(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "5") int limit) {
        List<SystemLog> logs = systemLogService.getUserLogs(userId, limit);
        
        List<SystemLogVO> voList = logs.stream().map(log -> {
            SystemLogVO vo = new SystemLogVO();
            BeanUtils.copyProperties(log, vo);
            return vo;
        }).collect(Collectors.toList());
        
        return Result.success(voList);
    }
}