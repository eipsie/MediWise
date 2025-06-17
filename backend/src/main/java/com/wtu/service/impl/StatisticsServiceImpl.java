package com.wtu.service.impl;

import com.wtu.mapper.DiagnosisMapper;
import com.wtu.mapper.DoctorMapper;
import com.wtu.mapper.PatientMapper;
import com.wtu.mapper.SystemLogMapper;
import com.wtu.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;
    private final DiagnosisMapper diagnosisMapper;
    private final SystemLogMapper systemLogMapper;

    @Override
    public Map<String, Object> getAdminDashboardStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 用户统计 - 使用doctorMapper代替userMapper
        int totalUsers = doctorMapper.countAllDoctors();
        int newUsersThisMonth = doctorMapper.countDoctorsByTimeRange(
                LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN),
                LocalDateTime.now()
        );
        
        // 角色统计
        int totalRoles = 2; // 管理员、医生两种固定角色
        
        // 日志统计
        int totalLogs = systemLogMapper.countAllLogs();
        int newLogsToday = systemLogMapper.countLogsByTimeRange(
                LocalDateTime.of(LocalDate.now(), LocalTime.MIN),
                LocalDateTime.now()
        );
        
        statistics.put("userCount", totalUsers);
        statistics.put("newUsersThisMonth", newUsersThisMonth);
        statistics.put("roleCount", totalRoles);
        statistics.put("logCount", totalLogs);
        statistics.put("newLogsToday", newLogsToday);
        
        return statistics;
    }

    @Override
    public Map<String, Object> getDoctorDashboardStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 当前月份的第一天和最后一天
        LocalDateTime firstDayOfMonth = LocalDateTime.of(
                LocalDate.now().withDayOfMonth(1), 
                LocalTime.MIN
        );
        LocalDateTime lastDayOfMonth = LocalDateTime.of(
                LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()),
                LocalTime.MAX
        );
        
        // 上个月的第一天和最后一天
        LocalDateTime firstDayOfLastMonth = LocalDateTime.of(
                LocalDate.now().minusMonths(1).withDayOfMonth(1),
                LocalTime.MIN
        );
        LocalDateTime lastDayOfLastMonth = LocalDateTime.of(
                LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()),
                LocalTime.MAX
        );
        
        // 患者统计
        int totalPatients = patientMapper.countAllPatients();
        int patientsThisMonth = patientMapper.countPatientsByTimeRange(firstDayOfMonth, lastDayOfMonth);
        int patientsLastMonth = patientMapper.countPatientsByTimeRange(firstDayOfLastMonth, lastDayOfLastMonth);
        double patientGrowthRate = patientsLastMonth > 0 
                ? (double) (patientsThisMonth - patientsLastMonth) / patientsLastMonth * 100 
                : 0.0;
        
        // 诊断统计
        int totalDiagnoses = diagnosisMapper.countAllDiagnoses();
        int diagnosesThisMonth = diagnosisMapper.countDiagnosesByTimeRange(firstDayOfMonth, lastDayOfMonth);
        int diagnosesLastMonth = diagnosisMapper.countDiagnosesByTimeRange(firstDayOfLastMonth, lastDayOfLastMonth);
        double diagnosisGrowthRate = diagnosesLastMonth > 0 
                ? (double) (diagnosesThisMonth - diagnosesLastMonth) / diagnosesLastMonth * 100 
                : 0.0;
        
        // AI调用统计
        int totalAICalls = systemLogMapper.countLogsByType("AI_CALL");
        int aiCallsThisMonth = systemLogMapper.countLogsByTypeAndTimeRange(
                "AI_CALL", firstDayOfMonth, lastDayOfMonth);
        int aiCallsLastMonth = systemLogMapper.countLogsByTypeAndTimeRange(
                "AI_CALL", firstDayOfLastMonth, lastDayOfLastMonth);
        double aiCallGrowthRate = aiCallsLastMonth > 0 
                ? (double) (aiCallsThisMonth - aiCallsLastMonth) / aiCallsLastMonth * 100 
                : 0.0;
        
        statistics.put("patientCount", totalPatients);
        statistics.put("patientGrowthRate", String.format("%.1f", patientGrowthRate));
        statistics.put("diagnosisCount", totalDiagnoses);
        statistics.put("diagnosisGrowthRate", String.format("%.1f", diagnosisGrowthRate));
        statistics.put("aiCallCount", totalAICalls);
        statistics.put("aiCallGrowthRate", String.format("%.1f", aiCallGrowthRate));
        
        return statistics;
    }
} 