package com.wtu.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 系统操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {
    /**
     * 操作类型
     */
    String actionType() default "";

    /**
     * 操作描述
     */
    String actionDesc() default "";
}
