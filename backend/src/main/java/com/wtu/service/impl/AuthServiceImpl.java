package com.wtu.service.impl;

import com.wtu.dto.RegisterDTO;
import com.wtu.entity.Doctor;
import com.wtu.mapper.AuthMapper;
import com.wtu.properties.JwtProperties;
import com.wtu.service.AuthService;
import com.wtu.utils.JwtUtil;
import com.wtu.utils.MD5Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthMapper authMapper;
    private final JwtProperties jwtProperties;

    /**
     * 医生登录
     */
    @Override
    public String login(String username, String password) {
        // 1. 根据用户名查询医生信息
        Doctor doctor = authMapper.getDoctorByUsername(username);
        
        // 2. 判断医生是否存在
        if (doctor == null) {
            return null;
        }
        
        // 3. 判断状态是否正常
        if (doctor.getStatus() != 1) {
            return null;
        }
        
        // 4. 使用MD5Util工具类验证密码
        if (!MD5Util.matches(password, doctor.getPassword())) {
            return null;
        }

        // 生成JWT令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", doctor.getId());
        claims.put("username", doctor.getUsername());
        claims.put("realName", doctor.getRealName());
        claims.put("role", doctor.getRole());

        // 直接返回JWT令牌
        return JwtUtil.createJwt(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims
        );
    }

    /**
     * 医生注册
     */
    @Override
    @Transactional
    public boolean register(RegisterDTO registerDTO) {
       // 1. 检查用户名是否已存在
       Doctor doctor = authMapper.getDoctorByUsername(registerDTO.getUsername());
       if(doctor == null) {
           // 2. 创建新医生对象并设置属性
           Doctor newDoctor = Doctor.builder()
                   .username(registerDTO.getUsername())
                   .password(MD5Util.encrypt(registerDTO.getPassword()))
                   .email(registerDTO.getEmail())
                   .role(registerDTO.getRole())
                   .department(registerDTO.getDepartment())
                   .build();
           // 3. 保存到数据库
           authMapper.register(newDoctor);
           return true;
       } else {
           // 4. 用户名已存在，注册失败
           return false;
       }
    }
} 