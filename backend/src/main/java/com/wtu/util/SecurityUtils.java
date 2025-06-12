package com.wtu.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 安全工具类
 */
@Slf4j
public class SecurityUtils {

    /**
     * 获取当前登录用户ID
     *
     * @return 当前用户ID
     */
    public static Long getCurrentUserId() {
        // 首先尝试从请求属性中获取用户ID（由JwtSecurityFilter设置）
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object userId = request.getAttribute("userId");
            log.debug("从请求属性中获取用户ID: {}", userId);
            if (userId != null) {
                try {
                    if (userId instanceof Number) {
                        return ((Number) userId).longValue();
                    } else if (userId instanceof String) {
                        return Long.parseLong((String) userId);
                    }
                } catch (NumberFormatException e) {
                    log.warn("无法将请求属性中的用户ID转换为Long: {}", userId, e);
                }
            }
        }

        // 如果请求属性中没有，尝试从认证对象中获取
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("当前认证对象: {}", authentication);
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("未获取到认证信息或认证未通过");
            return null;
        }

        Object principal = authentication.getPrincipal();
        log.debug("认证主体类型: {}, 值: {}", principal != null ? principal.getClass().getName() : "null", principal);
        
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            log.debug("从UserDetails获取到用户名: {}", username);
            // 假设用户名是数字ID
            try {
                return Long.parseLong(username);
            } catch (NumberFormatException e) {
                log.warn("无法将用户名转换为ID: {}", username, e);
                return null;
            }
        } else if (principal instanceof String) {
            log.debug("从String类型的Principal获取到用户名: {}", principal);
            try {
                return Long.parseLong((String) principal);
            } catch (NumberFormatException e) {
                log.warn("无法将用户名转换为ID: {}", principal, e);
                return null;
            }
        }

        log.warn("无法从认证对象中提取用户ID");
        return null;
    }

    /**
     * 获取当前登录用户名
     *
     * @return 当前用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        return principal.toString();
    }

    /**
     * 判断当前用户是否有指定角色
     *
     * @param role 角色名
     * @return 是否拥有该角色
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
    }

    /**
     * 判断当前用户是否是管理员
     *
     * @return 是否是管理员
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * 判断当前用户是否是医生
     *
     * @return 是否是医生
     */
    public static boolean isDoctor() {
        return hasRole("DOCTOR");
    }

    private SecurityUtils() {
        // 私有构造函数防止实例化
    }
} 