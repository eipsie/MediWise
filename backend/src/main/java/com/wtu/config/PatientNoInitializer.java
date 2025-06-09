package com.wtu.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wtu.entity.Patient;
import com.wtu.mapper.PatientMapper;
import com.wtu.utils.PatientNoGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 初始化患者编号生成器，确保系统重启后不会重复生成患者编号
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PatientNoInitializer implements CommandLineRunner {

    private final PatientMapper patientMapper;
    
    @Override
    public void run(String... args) {
        try {
            // 获取当前年份
            String currentYear = String.valueOf(LocalDate.now().getYear());
            
            // 构建查询条件：患者编号以P+当前年份开头
            LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.likeRight(Patient::getPatientNo, "P" + currentYear);
            queryWrapper.orderByDesc(Patient::getPatientNo);
            queryWrapper.last("LIMIT 1");
            
            // 查询最新的患者编号
            Patient latestPatient = patientMapper.selectOne(queryWrapper);
            
            if (latestPatient != null && StringUtils.hasText(latestPatient.getPatientNo())) {
                String patientNo = latestPatient.getPatientNo();
                // 解析患者编号，获取序号部分
                if (patientNo.length() >= 10) {
                    try {
                        int sequence = Integer.parseInt(patientNo.substring(5));
                        PatientNoGenerator.setCurrentSequence(sequence);
                        log.info("初始化患者编号生成器成功，当前序号: {}", sequence);
                    } catch (NumberFormatException e) {
                        log.error("解析患者编号序号失败: {}", patientNo, e);
                    }
                }
            } else {
                log.info("未找到当前年份的患者记录，患者编号生成器将从1开始");
            }
        } catch (Exception e) {
            log.error("初始化患者编号生成器时发生错误", e);
        }
    }
} 