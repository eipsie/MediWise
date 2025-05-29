package com.wtu.service;

import com.wtu.dto.RegisterDTO;
import com.wtu.entity.Doctor;

/**
 * 认证服务接口
 */
public interface AuthService {



    /**
     * 医生登录
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回医生信息，失败返回null
     */
    Doctor login(String username, String password);

    /**
     * 医生注册
     * @param registerDTO 注册信息(用户名、密码等)
     * @return 注册成功返回医生信息，失败返回null
     */
    Doctor register(RegisterDTO registerDTO);
}