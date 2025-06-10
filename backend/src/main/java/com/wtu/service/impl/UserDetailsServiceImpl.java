package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wtu.entity.Doctor;
import com.wtu.mapper.AuthMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthMapper authMapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("加载用户信息: {}", username);

        // 从数据库中查询用户信息
        LambdaQueryWrapper<Doctor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Doctor::getUsername, username);
        Doctor doctor = authMapper.selectOne(queryWrapper);

        if(doctor == null){
            log.info("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 判断账号状态
        if(doctor.getStatus() != 1){
            log.error("账号已被禁用: {}", username);
            throw new UsernameNotFoundException("账号已被禁用: " + username);
        }

        // 返回UserDetails对象
        return new User(
                doctor.getUsername(),
                doctor.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + doctor.getRole()))
        );
    }
}
