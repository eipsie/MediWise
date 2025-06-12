package com.wtu.service;

import com.wtu.entity.Doctor;
import com.wtu.entity.Patient;
import com.wtu.mapper.AuthMapper;
import com.wtu.mapper.PatientMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final PatientMapper patientMapper;
    private final AuthMapper authMapper;
    
    /**
     * 检查当前用户是否是患者的创建者
     * 用于@PreAuthorize("@securityService.isPatientCreator(#id, authentication.name)")
     */
    public boolean isPatientCreator(Long patientId, String username) {
        log.debug("检查用户[{}]是否是患者[{}]的创建者", username, patientId);
        
        // 查询患者
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            log.debug("患者不存在: {}", patientId);
            return false;
        }
        
        // 获取创建者ID
        Long creatorId = patient.getCreatorId();
        if (creatorId == null) {
            log.debug("患者没有创建者信息: {}", patientId);
            return false;
        }
        
        // 查询当前用户ID
        Long currentUserId = getCurrentUserId(username);
        if (currentUserId == null) {
            log.debug("当前用户不存在: {}", username);
            return false;
        }
        
        // 检查用户ID是否匹配创建者ID
        boolean isCreator = currentUserId.equals(creatorId);
        log.debug("用户[{}](ID:{})是患者[{}]的创建者(ID:{}): {}", username, currentUserId, patientId, creatorId, isCreator);
        return isCreator;
    }
    
    /**
     * 检查用户是否为管理员
     */
    public boolean isAdmin(String username) {
        Doctor doctor = getDoctorByUsername(username);
        return doctor != null && "ADMIN".equals(doctor.getRole());
    }
    
    /**
     * 根据用户名获取当前用户ID
     * @param username 用户名
     * @return 用户ID，如果用户不存在则返回null
     */
    public Long getCurrentUserId(String username) {
        Doctor doctor = getDoctorByUsername(username);
        return doctor != null ? doctor.getId() : null;
    }
    
    /**
     * 根据用户名获取医生信息
     */
    private Doctor getDoctorByUsername(String username) {
        // 使用MyBatis-Plus的LambdaQueryWrapper
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Doctor> queryWrapper = 
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.eq(Doctor::getUsername, username);
        return authMapper.selectOne(queryWrapper);
    }
} 