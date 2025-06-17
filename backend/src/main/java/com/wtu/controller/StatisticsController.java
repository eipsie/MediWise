package com.wtu.controller;

import com.wtu.result.Result;
import com.wtu.annotation.AdminOnly;
import com.wtu.annotation.OperationLog;
import com.wtu.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "统计数据", description = "系统统计数据接口")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/admin/dashboard")
    @AdminOnly
    @Operation(summary = "获取管理员控制台统计数据", description = "获取用户数量、日志数量等系统统计数据")
    @OperationLog(actionType = "STATISTICS_QUERY", actionDesc = "查询管理员控制台统计数据")
    public Result<Map<String, Object>> getAdminDashboardStatistics() {
        Map<String, Object> statistics = statisticsService.getAdminDashboardStatistics();
        return Result.success(statistics);
    }

    @GetMapping("/doctor/dashboard")
    @Operation(summary = "获取医生控制台统计数据", description = "获取医生相关的患者数量、诊断数量等统计数据")
    @OperationLog(actionType = "STATISTICS_QUERY", actionDesc = "查询医生控制台统计数据")
    public Result<Map<String, Object>> getDoctorDashboardStatistics() {
        Map<String, Object> statistics = statisticsService.getDoctorDashboardStatistics();
        return Result.success(statistics);
    }
} 