package com.wtu.constant;

/**
 * 系统日志常量类
 */
public class LogConstant {

    /**
     * 日志类型
     */
    public static class LogType {
        /** 操作日志 */
        public static final String OPERATION = "OPERATION";
        /** 错误日志 */
        public static final String ERROR = "ERROR";
        /** AI调用日志 */
        public static final String AI_CALL = "AI_CALL";
    }

    /**
     * 日志状态
     */
    public static class LogStatus {
        /** 成功 */
        public static final String SUCCESS = "SUCCESS";
        /** 失败 */
        public static final String FAILURE = "FAILURE";
        /** 错误 */
        public static final String ERROR = "ERROR";
    }

    /**
     * 操作类型
     */
    public static class ActionType {
        /** 用户登录 */
        public static final String USER_LOGIN = "USER_LOGIN";
        /** 用户退出 */
        public static final String USER_LOGOUT = "USER_LOGOUT";
        /** 用户查询 */
        public static final String USER_QUERY = "USER_QUERY";
        /** 用户创建 */
        public static final String USER_CREATE = "USER_CREATE";
        /** 用户更新 */
        public static final String USER_UPDATE = "USER_UPDATE";
        /** 用户删除 */
        public static final String USER_DELETE = "USER_DELETE";
        /** 用户启用/禁用 */
        public static final String USER_STATUS_CHANGE = "USER_STATUS_CHANGE";
        /** 密码重置 */
        public static final String PASSWORD_RESET = "PASSWORD_RESET";
        
        /** 患者查询 */
        public static final String PATIENT_QUERY = "PATIENT_QUERY";
        /** 患者创建 */
        public static final String PATIENT_CREATE = "PATIENT_CREATE";
        /** 患者更新 */
        public static final String PATIENT_UPDATE = "PATIENT_UPDATE";
        /** 患者删除 */
        public static final String PATIENT_DELETE = "PATIENT_DELETE";
        
        /** 诊断创建 */
        public static final String DIAGNOSIS_CREATE = "DIAGNOSIS_CREATE";
        /** 诊断更新 */
        public static final String DIAGNOSIS_UPDATE = "DIAGNOSIS_UPDATE";
        /** 诊断查询 */
        public static final String DIAGNOSIS_QUERY = "DIAGNOSIS_QUERY";
        
        /** 系统设置更新 */
        public static final String SYSTEM_CONFIG_UPDATE = "SYSTEM_CONFIG_UPDATE";
        /** 日志查询 */
        public static final String LOG_QUERY = "LOG_QUERY";
        
        /** AI诊断调用 */
        public static final String AI_DIAGNOSIS_CALL = "AI_DIAGNOSIS_CALL";
    }
} 