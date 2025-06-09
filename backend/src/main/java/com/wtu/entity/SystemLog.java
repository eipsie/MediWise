package com.wtu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统日志实体类
 * 对应数据库中的system_log表
 */
@Data
@TableName("system_log")
@Schema(description = "系统日志实体")
public class SystemLog {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "日志ID", example = "1")
    private Long id;
    
    /**
     * 日志类型
     */
    @Schema(description = "日志类型：OPERATION/ERROR/AI_CALL", example = "OPERATION")
    private String logType;
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    
    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "doctor_zhang")
    private String username;
    
    /**
     * 操作类型
     */
    @Schema(description = "操作类型", example = "USER_LOGIN")
    private String actionType;
    
    /**
     * 操作描述
     */
    @Schema(description = "操作描述", example = "用户登录系统")
    private String actionDesc;
    
    /**
     * 操作对象ID
     */
    @Schema(description = "操作对象ID", example = "10")
    private String targetId;
    
    /**
     * IP地址
     */
    @Schema(description = "IP地址", example = "192.168.1.100")
    private String ipAddress;
    
    /**
     * 状态
     */
    @Schema(description = "状态：SUCCESS/FAILED/ERROR", example = "SUCCESS")
    private String status;
    
    /**
     * 错误信息
     */
    @Schema(description = "错误信息", example = "用户名或密码错误")
    private String errorMessage;
    
    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    private LocalDateTime logTime;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
} 