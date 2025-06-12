package com.wtu.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个方法只有管理员或医生角色的用户可以访问
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
public @interface AdminOrDoctor {
} 