package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wtu.dto.auth.RegisterDTO;
import com.wtu.entity.Doctor;
import com.wtu.mapper.AuthMapper;
import com.wtu.properties.JwtProperties;
import com.wtu.service.AuthService;
import com.wtu.utils.JwtUtil;
import com.wtu.utils.MD5Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.Doc;
import java.time.LocalDateTime;
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
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    /**
     * 医生登录
     */
    @Override
    public String login(String username, String password) {
        try {
            
            // 使用Spring Security的AuthenticationManager进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // 认证成功, 查询用户详情
            LambdaQueryWrapper<Doctor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Doctor::getUsername, username);
            Doctor doctor = authMapper.selectOne(queryWrapper);

            // 生成JWT令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", doctor.getId());
            claims.put("username", doctor.getUsername());
            claims.put("realName", doctor.getRealName());
            claims.put("role", doctor.getRole());

            // 返回JWT
            return JwtUtil.createJwt(
                    jwtProperties.getSecretKey(),
                    jwtProperties.getTtl(),
                    claims
            );
        } catch (AuthenticationException e){
            log.error("登录失败: {}", e.getMessage());
            return null; // 登录失败返回null
        }
    }

    /**
     * 医生注册
     */
    @Override
    @Transactional
    public boolean register(RegisterDTO registerDTO) {
       // 1. 检查用户名是否已存在
       LambdaQueryWrapper<Doctor> queryWrapper = new LambdaQueryWrapper<>();
       queryWrapper.eq(Doctor::getUsername, registerDTO.getUsername());
       Doctor existingDoctor = authMapper.selectOne(queryWrapper);
       
       if(existingDoctor == null) {
           // 2. 创建新医生对象并设置属性
           Doctor newDoctor = Doctor.builder()
                   .username(registerDTO.getUsername())
                   .password(passwordEncoder.encode(registerDTO.getPassword())) // 使用Spring Security的BCrypt加密
                   .email(registerDTO.getEmail())
                   .role(registerDTO.getRole())
                   .department(registerDTO.getDepartment())
                   .status(1) // 默认为启用状态
                   .createTime(LocalDateTime.now())
                   .updateTime(LocalDateTime.now())
                   .build();
           
           // 3. 保存到数据库
           int rows = authMapper.insert(newDoctor);
           return rows > 0;
       } else {
           // 4. 用户名已存在，注册失败
           return false;
       }
    }
} 