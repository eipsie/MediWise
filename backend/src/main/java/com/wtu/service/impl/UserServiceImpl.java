package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.wtu.VO.user.DoctorVO;
import com.wtu.dto.user.ChangePasswordDTO;
import com.wtu.dto.user.UpdateProfileDTO;
import com.wtu.dto.user.UserStatusDTO;
import com.wtu.entity.Doctor;
import com.wtu.exception.BusinessException;
import com.wtu.mapper.AuthMapper;
import com.wtu.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public DoctorVO getCurrentUserProfile(String username) {
        Doctor doctor = getDoctorByUsername(username);
        return convertToDoctorVO(doctor);
    }
    
    @Override
    @Transactional
    public DoctorVO updateUserProfile(String username, UpdateProfileDTO updateProfileDTO) {
        // 获取当前用户
        Doctor doctor = getDoctorByUsername(username);
        if (doctor == null) {
            log.error("用户不存在: {}", username);
            throw new BusinessException("用户不存在");
        }
        
        // 更新用户信息
        doctor.setRealName(updateProfileDTO.getRealName());
        doctor.setDepartment(updateProfileDTO.getDepartment());
        doctor.setTitle(updateProfileDTO.getTitle());
        doctor.setEmail(updateProfileDTO.getEmail());
        doctor.setPhone(updateProfileDTO.getPhone());
        doctor.setUpdateTime(LocalDateTime.now());
        
        // 保存更新
        authMapper.updateById(doctor);
        
        return convertToDoctorVO(doctor);
    }
    
    @Override
    @Transactional
    public boolean changePassword(String username, ChangePasswordDTO changePasswordDTO) {
        // 获取当前用户
        Doctor doctor = getDoctorByUsername(username);
        if (doctor == null) {
            log.error("用户不存在: {}", username);
            throw new BusinessException("用户不存在");
        }
        
        // 验证新密码与确认密码是否一致
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            log.error("新密码与确认密码不一致");
            throw new BusinessException("新密码与确认密码不一致");
        }
        
        // 验证旧密码是否正确
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), doctor.getPassword())) {
            log.error("旧密码不正确");
            throw new BusinessException("旧密码不正确");
        }
        
        // 更新新密码
        doctor.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        doctor.setUpdateTime(LocalDateTime.now());
        
        // 保存更新
        authMapper.updateById(doctor);
        
        return true;
    }
    
    @Override
    public List<DoctorVO> getAllUsers() {
        // 查询所有用户
        List<Doctor> doctors = authMapper.selectList(null);
        
        // 转换为VO
        return doctors.stream()
                .map(this::convertToDoctorVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public DoctorVO getUserById(Long userId) {
        Doctor doctor = authMapper.selectById(userId);
        if (doctor == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToDoctorVO(doctor);
    }
    
    @Override
    @Transactional
    public boolean updateUserStatus(Long userId, UserStatusDTO statusDTO) {
        // 获取用户
        Doctor doctor = authMapper.selectById(userId);
        if (doctor == null) {
            log.error("用户不存在: {}", userId);
            throw new BusinessException("用户不存在");
        }
        
        // 检查状态值是否有效
        if (statusDTO.getStatus() != 0 && statusDTO.getStatus() != 1) {
            log.error("无效的状态值: {}", statusDTO.getStatus());
            throw new BusinessException("无效的状态值，只能为0(禁用)或1(启用)");
        }
        
        // 更新用户状态
        doctor.setStatus(statusDTO.getStatus());
        doctor.setUpdateTime(LocalDateTime.now());
        
        // 保存更新
        authMapper.updateById(doctor);
        
        return true;
    }
    
    @Override
    @Transactional
    public String resetUserPassword(Long userId) {
        // 获取用户
        Doctor doctor = authMapper.selectById(userId);
        if (doctor == null) {
            log.error("用户不存在: {}", userId);
            throw new BusinessException("用户不存在");
        }
        
        // 生成随机密码 (8位字母数字组合)
        String newPassword = RandomStringUtils.randomAlphanumeric(8);
        
        // 更新密码
        doctor.setPassword(passwordEncoder.encode(newPassword));
        doctor.setUpdateTime(LocalDateTime.now());
        
        // 保存更新
        authMapper.updateById(doctor);
        
        // 返回新密码（明文，用于显示给管理员）
        return newPassword;
    }
    
    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        // 获取用户
        Doctor doctor = authMapper.selectById(userId);
        if (doctor == null) {
            log.error("用户不存在: {}", userId);
            throw new BusinessException("用户不存在");
        }
        
        // 检查是否为最后一个管理员
        if ("ADMIN".equals(doctor.getRole())) {
            // 查询管理员数量
            LambdaQueryWrapper<Doctor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Doctor::getRole, "ADMIN");
            Long adminCount = authMapper.selectCount(queryWrapper);
            
            if (adminCount <= 1) {
                log.error("无法删除最后一个管理员");
                throw new BusinessException("无法删除最后一个管理员");
            }
        }
        
        // 删除用户
        authMapper.deleteById(userId);
        
        return true;
    }
    
    /**
     * 根据用户名获取医生信息
     */
    private Doctor getDoctorByUsername(String username) {
        LambdaQueryWrapper<Doctor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Doctor::getUsername, username);
        return authMapper.selectOne(queryWrapper);
    }
    
    /**
     * 将Doctor实体转换为DoctorVO
     */
    private DoctorVO convertToDoctorVO(Doctor doctor) {
        if (doctor == null) {
            return null;
        }
        
        DoctorVO doctorVO = new DoctorVO();
        BeanUtils.copyProperties(doctor, doctorVO);
        // 不返回密码等敏感信息
        return doctorVO;
    }
} 