package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wtu.entity.Patient;
import com.wtu.exception.BusinessException;
import com.wtu.exception.PatientValidationException;
import com.wtu.exception.ResourceNotFoundException;
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
    public Patient createPatient(Patient patient) {
        // 1. 数据验证 - 现在会抛出异常而不是返回false
        validatePatient(patient);
        
        // 2. 生成患者编号
        patient.setPatientNo(PatientNoGenerator.generatePatientNo());
        
        // 3. 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        patient.setCreateTime(now);
        patient.setUpdateTime(now);
        
        // 4. 插入数据库
        int rows = patientMapper.insert(patient);
        
        if (rows <= 0) {
            throw new BusinessException("创建患者记录失败");
        }
        
        log.info("创建患者成功: {}", patient.getPatientNo());
        return patient;  // 返回创建的患者对象，包含ID
    }

    @Override
    @Transactional
    public Patient updatePatient(Patient patient) {
        // 1. 数据验证
        validatePatient(patient);
        
        // 2. 查询患者是否存在
        Patient existingPatient = patientMapper.selectById(patient.getId());
        if (existingPatient == null) {
            throw new ResourceNotFoundException("患者不存在，ID: " + patient.getId());
        }
        
        // 3. 设置不能修改的字段
        patient.setPatientNo(existingPatient.getPatientNo());
        patient.setCreatorId(existingPatient.getCreatorId());
        patient.setCreateTime(existingPatient.getCreateTime());
        
        // 4. 设置更新时间
        patient.setUpdateTime(LocalDateTime.now());
        
        // 5. 更新数据库
        int rows = patientMapper.updateById(patient);
        
        if (rows <= 0) {
            throw new BusinessException("更新患者记录失败");
        }
        
        log.info("更新患者成功: {}", patient.getPatientNo());
        return patient;  // 返回更新后的患者对象
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        // 1. 查询患者是否存在
        Patient patient = patientMapper.selectById(id);
        if (patient == null) {
            throw new ResourceNotFoundException("患者不存在，ID: " + id);
        }
        
        // 2. 删除患者
        int rows = patientMapper.deleteById(id);
        
        if (rows <= 0) {
            throw new BusinessException("删除患者记录失败");
        }
        
        log.info("删除患者成功: {}", patient.getPatientNo());
    }

    @Override
    public Patient getPatientById(Long id) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null) {
            throw new ResourceNotFoundException("患者不存在，ID: " + id);
        }
        return patient;
    }

    @Override
    public Patient getPatientByPatientNo(String patientNo) {
        if (!StringUtils.hasText(patientNo)) {
            throw new PatientValidationException("患者编号不能为空");
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
            throw new PatientValidationException("医生ID不能为空");
        }
        
        LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Patient::getCreatorId, doctorId);
        queryWrapper.orderByDesc(Patient::getCreateTime);
        
        return patientMapper.selectList(queryWrapper);
    }
    
    /**
     * 验证患者数据的有效性
     * @param patient 患者对象
     * @throws PatientValidationException 如果验证失败
     */
    private void validatePatient(Patient patient) {
        // 检查基本字段
        if (patient == null || !StringUtils.hasText(patient.getName())) {
            throw new PatientValidationException("患者姓名不能为空");
        }
        
        // 检查性别取值范围
        if (patient.getGender() == null || patient.getGender() < 0 || patient.getGender() > 2) {
            throw new PatientValidationException("患者性别取值无效");
        }
        
        // 验证手机号格式（如果有）
        if (StringUtils.hasText(patient.getPhone()) && !PHONE_PATTERN.matcher(patient.getPhone()).matches()) {
            throw new PatientValidationException("手机号格式不正确: " + patient.getPhone());
        }
        
        // 验证身份证号格式（如果有）
        if (StringUtils.hasText(patient.getIdCard()) && !ID_CARD_PATTERN.matcher(patient.getIdCard()).matches()) {
            throw new PatientValidationException("身份证号格式不正确: " + patient.getIdCard());
        }
    }
} 