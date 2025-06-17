package com.wtu.aspect;

import com.alibaba.fastjson.JSON;
import com.wtu.annotation.OperationLog;
import com.wtu.constant.LogConstant;
import com.wtu.entity.SystemLog;
import com.wtu.service.SystemLogService;
import com.wtu.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final SystemLogService systemLogService;

    /**
     * 定义切入点
     */
    @Pointcut("@annotation(com.wtu.annotation.OperationLog)")
    public void operationLogPointcut(){}

    /**
     * 环绕通知，可以获取请求参数和响应结果
     */
    @Around("operationLogPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            // 记录日志
            saveOperationLog(joinPoint, null, result, System.currentTimeMillis() - startTime);
            return result;
        } catch (Throwable e) {
            // 记录异常日志
            saveOperationLog(joinPoint, e, null, System.currentTimeMillis() - startTime);
            throw e;
        }
    }

    /**
     * 保存操作日志
     */
    private void saveOperationLog(JoinPoint joinPoint, Throwable e, Object result, long executionTime) {
        try {
            // 获取注解信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperationLog operationLog = method.getAnnotation(OperationLog.class);

            // 创建日志对象
            SystemLog systemLog = new SystemLog();

            // 设置日志类型和状态
            if(e != null) {
                systemLog.setLogType(LogConstant.LogType.ERROR);
                systemLog.setStatus(LogConstant.LogStatus.FAILURE);
                systemLog.setErrorMessage(e.getMessage());
            } else {
                systemLog.setLogType(LogConstant.LogType.OPERATION);
                systemLog.setStatus(LogConstant.LogStatus.SUCCESS);
            }

            // 设置操作类型和描述
            systemLog.setActionType(operationLog.actionType());
            systemLog.setActionDesc(operationLog.actionDesc());
            
            // 设置执行时间
            systemLog.setExecutionTime(executionTime);

            // 获取请求IP和请求路径
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                systemLog.setIpAddress(getIpAddress(request));
                
                // 设置请求URL和请求方法
                systemLog.setRequestUrl(request.getRequestURI());
                systemLog.setRequestMethod(request.getMethod());
                
                // 提取请求参数（可以记录到actionDesc中或单独字段）
                Map<String, Object> requestParams = extractRequestParams(joinPoint);
                if (!requestParams.isEmpty()) {
                    String paramsJson = JSON.toJSONString(requestParams);
                    // 可以在描述中添加重要参数
                    String originalDesc = systemLog.getActionDesc();
                    if (paramsJson.length() <= 200) {  // 限制长度
                        systemLog.setActionDesc(originalDesc + ", 参数: " + paramsJson);
                    }
                    log.debug("Request parameters: {}", paramsJson);
                }
            }
            
            // 记录操作结果摘要
            if (result != null) {
                try {
                    String resultJson = JSON.toJSONString(result);
                    if (resultJson.length() > 500) {
                        resultJson = resultJson.substring(0, 500) + "...";
                    }
                    systemLog.setOperationResult(resultJson);
                    log.debug("Operation result: {}", resultJson);
                } catch (Exception ex) {
                    log.warn("Failed to serialize operation result", ex);
                }
            }

            // 设置用户信息
            try {
                Long userId = SecurityUtils.getCurrentUserId();
                String username = SecurityUtils.getCurrentUsername();
                systemLog.setUserId(userId);
                systemLog.setUsername(username);
            } catch (Exception ex) {
                log.warn("获取当前用户信息失败", ex);
            }

            systemLog.setLogTime(LocalDateTime.now());

            // 保存日志
            systemLogService.saveLog(systemLog);
        } catch (Exception ex) {
            log.error("记录操作日志失败", ex);
        }
    }

    /**
     * 提取请求参数
     */
    private Map<String, Object> extractRequestParams(JoinPoint joinPoint) {
        Map<String, Object> requestParams = new HashMap<>();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();
        
        if (paramNames != null && paramValues != null) {
            for (int i = 0; i < paramNames.length; i++) {
                Object value = paramValues[i];
                // 过滤掉文件上传等无法序列化的参数
                if (value != null && !(value instanceof MultipartFile)) {
                    try {
                        requestParams.put(paramNames[i], value);
                    } catch (Exception e) {
                        requestParams.put(paramNames[i], "无法序列化的参数类型");
                    }
                }
            }
        }
        return requestParams;
    }

    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}














