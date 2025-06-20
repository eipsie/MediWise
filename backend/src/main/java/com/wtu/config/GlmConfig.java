package com.wtu.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * GLM配置类
 */
@Getter
@Configuration
public class GlmConfig {

    /**
     * GLM API密钥
     */
    @Value("${glm.api-key}")
    private String apiKey;
    
    /**
     * GLM API基础URL
     */
    @Value("${glm.url}")
    private String url;

} 