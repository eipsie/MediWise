package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wtu.entity.Patient;
import com.wtu.mapper.PatientMapper;
import com.wtu.service.PatientService;
import com.wtu.utils.PatientNoGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 患者服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientMapper patientMapper;
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$");

    @Override
    @Transactional
    public boolean createPatient(Patient patient) {
        // 1. 数据验证
        if (!validatePatient(patient)) {
            return false;
        }
        
        // 2. 生成患者编号
        patient.setPatientNo(PatientNoGenerator.generatePatientNo());
        
        // 3. 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        patient.setCreateTime(now);
        patient.setUpdateTime(now);
        
        // 4. 插入数据库
        int rows = patientMapper.insert(patient);
        
        log.info("创建患者成功: {}", patient.getPatientNo());
        return rows > 0;
    }

    @Override
    @Transactional
    public boolean updatePatient(Patient patient) {
        // 1. 数据验证
        if (!validatePatient(patient)) {
            return false;
        }
        
        // 2. 查询患者是否存在
        Patient existingPatient = patientMapper.selectById(patient.getId());
        if (existingPatient == null) {
            log.error("更新患者失败: 患者ID不存在 {}", patient.getId());
            return false;
        }
        
        // 3. 设置不能修改的字段
        patient.setPatientNo(existingPatient.getPatientNo());
        patient.setCreatorId(existingPatient.getCreatorId());
        patient.setCreateTime(existingPatient.getCreateTime());
        
        // 4. 设置更新时间
        patient.setUpdateTime(LocalDateTime.now());
        
        // 5. 更新数据库
        int rows = patientMapper.updateById(patient);
        
        log.info("更新患者成功: {}", patient.getPatientNo());
        return rows > 0;
    }

    @Override
    @Transactional
    public boolean deletePatient(Long id) {
        // 1. 查询患者是否存在
        Patient patient = patientMapper.selectById(id);
        if (patient == null) {
            log.error("删除患者失败: 患者ID不存在 {}", id);
            return false;
        }
        
        // 2. 删除患者
        int rows = patientMapper.deleteById(id);
        
        log.info("删除患者成功: {}", patient.getPatientNo());
        return rows > 0;
    }

    @Override
    public Patient getPatientById(Long id) {
        return patientMapper.selectById(id);
    }

    @Override
    public Patient getPatientByPatientNo(String patientNo) {
        if (!StringUtils.hasText(patientNo)) {
            return null;
        }
        
        LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Patient::getPatientNo, patientNo);
        
        return patientMapper.selectOne(queryWrapper);
    }

    @Override
    public IPage<Patient> pagePatients(Page<Patient> page, String name, String idCard, String phone) {
        LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        queryWrapper.like(StringUtils.hasText(name), Patient::getName, name)
                .like(StringUtils.hasText(idCard), Patient::getIdCard, idCard)
                .like(StringUtils.hasText(phone), Patient::getPhone, phone);
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(Patient::getCreateTime);
        
        return patientMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<Patient> getPatientsByDoctorId(Long doctorId) {
        if (doctorId == null) {
            return null;
        }
        
        LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Patient::getCreatorId, doctorId);
        queryWrapper.orderByDesc(Patient::getCreateTime);
        
        return patientMapper.selectList(queryWrapper);
    }
    
    /**
     * 验证患者数据的有效性
     * @param patient 患者对象
     * @return 是否有效
     */
    private boolean validatePatient(Patient patient) {
        // 检查基本字段
        if (patient == null || !StringUtils.hasText(patient.getName())) {
            log.error("患者姓名不能为空");
            return false;
        }
        
        // 检查性别取值范围
        if (patient.getGender() == null || patient.getGender() < 0 || patient.getGender() > 2) {
            log.error("患者性别取值无效");
            return false;
        }
        
        // 验证手机号格式（如果有）
        if (StringUtils.hasText(patient.getPhone()) && !PHONE_PATTERN.matcher(patient.getPhone()).matches()) {
            log.error("手机号格式不正确: {}", patient.getPhone());
            return false;
        }
        
        // 验证身份证号格式（如果有）
        if (StringUtils.hasText(patient.getIdCard()) && !ID_CARD_PATTERN.matcher(patient.getIdCard()).matches()) {
            log.error("身份证号格式不正确: {}", patient.getIdCard());
            return false;
        }
        
        return true;
    }
} 