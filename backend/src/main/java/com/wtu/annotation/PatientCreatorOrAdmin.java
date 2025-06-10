package com.wtu.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记为患者创建者或管理员可以访问的方法
 * 要求方法中有一个名为id的参数，代表患者ID
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN') or @securityService.isPatientCreator(#id, authentication.name)")
public @interface PatientCreatorOrAdmin {
} 