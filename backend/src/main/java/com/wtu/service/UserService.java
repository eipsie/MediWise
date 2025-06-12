package com.wtu.service;

import com.wtu.VO.user.DoctorVO;
import com.wtu.dto.user.ChangePasswordDTO;
import com.wtu.dto.user.UpdateProfileDTO;
import com.wtu.dto.user.UserStatusDTO;
import com.wtu.entity.Doctor;

import java.util.List;

/**
 * 用户管理服务接口
 */
public interface UserService {
    
    /**
     * 获取当前登录用户的个人信息
     * @param username 用户名
     * @return 用户信息视图对象
     */
    DoctorVO getCurrentUserProfile(String username);
    
    /**
     * 更新当前用户个人信息
     * @param username 用户名
     * @param updateProfileDTO 更新信息DTO
     * @return 更新后的用户信息
     */
    DoctorVO updateUserProfile(String username, UpdateProfileDTO updateProfileDTO);
    
    /**
     * 修改密码
     * @param username 用户名
     * @param changePasswordDTO 密码修改DTO
     * @return 是否修改成功
     */
    boolean changePassword(String username, ChangePasswordDTO changePasswordDTO);
    
    /**
     * 获取所有用户列表（管理员功能）
     * @return 用户列表
     */
    List<DoctorVO> getAllUsers();
    
    /**
     * 根据ID获取用户信息（管理员功能）
     * @param userId 用户ID
     * @return 用户信息
     */
    DoctorVO getUserById(Long userId);
    
    /**
     * 更新用户状态：启用/禁用（管理员功能）
     * @param userId 用户ID
     * @param statusDTO 状态DTO
     * @return 是否更新成功
     */
    boolean updateUserStatus(Long userId, UserStatusDTO statusDTO);
    
    /**
     * 重置用户密码（管理员功能）
     * @param userId 用户ID
     * @return 新密码
     */
    String resetUserPassword(Long userId);
    
    /**
     * 删除用户（管理员功能）
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long userId);
} 