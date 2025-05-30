package com.wtu.controller;

import com.wtu.VO.LoginVO;
import com.wtu.VO.RegisterVO;
import com.wtu.dto.LoginDTO;
import com.wtu.dto.RegisterDTO;
import com.wtu.entity.Doctor;
import com.wtu.result.Result;
import com.wtu.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "登录接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 医生登录接口
     * @param loginDTO 登录参数(用户名和密码)
     * @return 登录结果，成功返回医生信息，失败返回错误信息
     */
    @Operation(summary = "医生登录", description = "用户名密码登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Parameter(description = "登录信息") @RequestBody LoginDTO loginDTO) {
        // 调用服务层进行登录验证
        LoginVO loginVO = authService.login(loginDTO.getUsername(), loginDTO.getPassword());
        
        // 登录成功
        if (loginVO != null) {
            return Result.success(loginVO);
        } else {
            // 登录失败，返回错误信息
            return Result.error("用户名或密码错误");
        }

    }

    /**
     * 医生注册接口
     * @param registerDTO 注册参数(用户名、密码等)
     * @return 注册结果，成功返回医生信息，失败返回错误信息
     */
    @Operation(summary = "医生注册", description = "新用户注册")
    @PostMapping("/register")
    public Result<RegisterVO> register(@Parameter(description = "注册信息") @RequestBody RegisterDTO registerDTO) {
        Doctor doctor = authService.register(registerDTO);
        if(doctor != null) {
            RegisterVO registerVO = new RegisterVO();
            BeanUtils.copyProperties(doctor, registerVO);
            return Result.success(registerVO);
        }else{
            return Result.error("用户名重复！！");
        }
    }
} 