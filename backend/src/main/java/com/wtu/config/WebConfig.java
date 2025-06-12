package com.wtu.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.wtu.interceptor.JwtTokenInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * Web相关配置
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许的源
                .allowedOriginPatterns("*")
                // 允许的方法
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                // 允许的请求头
                .allowedHeaders("*")
                // 允许携带凭证
                .allowCredentials(true);
    }

    /**
     * 提供ObjectMapper Bean
     * 配置支持Java 8日期时间类型(JSR-310)
     * @return ObjectMapper实例
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 注册Java 8日期时间模块
        objectMapper.registerModule(new JavaTimeModule());
        // 禁用将日期序列化为时间戳的功能，使用ISO-8601格式
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }
}
