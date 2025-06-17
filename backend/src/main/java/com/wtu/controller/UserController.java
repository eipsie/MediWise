package com.wtu.controller;

import com.wtu.VO.user.DoctorVO;
import com.wtu.annotation.AdminOnly;
import com.wtu.annotation.AdminOrDoctor;
import com.wtu.annotation.OperationLog;
import com.wtu.constant.LogConstant;
import com.wtu.dto.user.ChangePasswordDTO;
import com.wtu.dto.user.UpdateProfileDTO;
import com.wtu.dto.user.UserStatusDTO;
import com.wtu.result.Result;
import com.wtu.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "用户管理", description = "用户信息管理相关接口")
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @AdminOrDoctor
    @Operation(summary = "获取当前用户个人信息", description = "获取当前登录用户的个人信息")
    @OperationLog(actionType = LogConstant.ActionType.USER_QUERY, actionDesc = "查看个人信息")
    public Result<DoctorVO> getCurrentUserProfile(Authentication authentication) {
        DoctorVO doctorVO = userService.getCurrentUserProfile(authentication.getName());
        return Result.success(doctorVO);
    }
    
    @PutMapping("/profile")
    @AdminOrDoctor
    @Operation(summary = "更新当前用户个人信息", description = "更新当前登录用户的个人信息")
    @OperationLog(actionType = LogConstant.ActionType.USER_UPDATE, actionDesc = "更新个人信息")
    public Result<DoctorVO> updateUserProfile(
            Authentication authentication,
            @RequestBody @Valid UpdateProfileDTO updateProfileDTO) {
        DoctorVO doctorVO = userService.updateUserProfile(authentication.getName(), updateProfileDTO);
        return Result.success(doctorVO);
    }
    
    @PutMapping("/password")
    @AdminOrDoctor
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    @OperationLog(actionType = LogConstant.ActionType.PASSWORD_RESET, actionDesc = "修改个人密码")
    public Result<Boolean> changePassword(
            Authentication authentication,
            @RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(authentication.getName(), changePasswordDTO);
        return Result.success(true);
    }
    
    @GetMapping
    @AdminOnly
    @Operation(summary = "获取所有用户列表", description = "管理员功能：获取所有用户列表")
    @OperationLog(actionType = LogConstant.ActionType.USER_QUERY, actionDesc = "管理员查询所有医生")
    public Result<List<DoctorVO>> getAllUsers() {
        List<DoctorVO> users = userService.getAllUsers();
        return Result.success(users);
    }
    
    @GetMapping("/{id}")
    @AdminOnly
    @Operation(summary = "获取指定用户信息", description = "管理员功能：根据ID获取指定用户信息")
    @OperationLog(actionType = LogConstant.ActionType.USER_QUERY, actionDesc = "管理员查询指定医生信息")
    public Result<DoctorVO> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable("id") Long userId) {
        DoctorVO doctorVO = userService.getUserById(userId);
        return Result.success(doctorVO);
    }
    
    @PutMapping("/{id}/status")
    @AdminOnly
    @Operation(summary = "更新用户状态", description = "管理员功能：启用或禁用指定用户")
    @OperationLog(actionType = LogConstant.ActionType.USER_STATUS_CHANGE, actionDesc = "管理员更新医生状态")
    public Result<Boolean> updateUserStatus(
            @Parameter(description = "用户ID", required = true)
            @PathVariable("id") Long userId,
            @RequestBody @Valid UserStatusDTO statusDTO) {
        userService.updateUserStatus(userId, statusDTO);
        return Result.success(true);
    }
    
    @PostMapping("/{id}/reset-password")
    @AdminOnly
    @Operation(summary = "重置用户密码", description = "管理员功能：重置指定用户的密码")
    @OperationLog(actionType = LogConstant.ActionType.PASSWORD_RESET, actionDesc = "管理员重置医生密码")
    public Result<String> resetUserPassword(
            @Parameter(description = "用户ID", required = true)
            @PathVariable("id") Long userId) {
        String newPassword = userService.resetUserPassword(userId);
        return Result.success(newPassword);
    }
    
    @DeleteMapping("/{id}")
    @AdminOnly
    @Operation(summary = "删除用户", description = "管理员功能：删除指定用户")
    @OperationLog(actionType = LogConstant.ActionType.USER_DELETE, actionDesc = "管理员删除医生")
    public Result<Boolean> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable("id") Long userId) {
        userService.deleteUser(userId);
        return Result.success(true);
    }
}
