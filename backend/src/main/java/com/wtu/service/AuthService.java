package com.wtu.service;

import com.wtu.dto.auth.RegisterDTO;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 医生登录
     * @param username 用户名
     * @param password 密码
     * @return JWT令牌字符串，登录失败返回null
     */
    String login(String username, String password);
    
    /**
     * 医生注册
     * @param registerDTO 注册数据传输对象
     * @return 注册是否成功
     */
    boolean register(RegisterDTO registerDTO);
}