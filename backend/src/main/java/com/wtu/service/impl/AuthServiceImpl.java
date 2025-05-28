package com.wtu.service.impl;

import com.wtu.entity.Doctor;
import com.wtu.mapper.AuthMapper;
import com.wtu.service.AuthService;
import com.wtu.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthMapper authMapper;

    /**
     * 医生登录
     */
    @Override
    public Doctor login(String username, String password) {
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
        
        // 5. 登录成功，清空密码后返回医生信息
        doctor.setPassword(null);
        return doctor;
    }
} 