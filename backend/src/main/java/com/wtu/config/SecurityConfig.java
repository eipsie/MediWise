package com.wtu.config;


import com.wtu.filter.JwtSecurityFilter;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtSecurityFilter jwtSecurityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 定义不需要认证的公共路径
        List<String> permitAllPaths = Arrays.asList(
                "/api/auth/login",
                "/api/auth/register",
                "/doc.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/webjars/**"
        );

        http
                .csrf(AbstractHttpConfigurer::disable) // 禁用CSRF保护
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable) // 禁用表单登录
                .httpBasic(AbstractHttpConfigurer::disable) // 禁用HTTP基本认证
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(permitAllPaths.toArray(new String[0])).permitAll() // 允许指定路径不需要认证
                        .anyRequest().authenticated() // 其他所有请求都需要认证，具体权限由方法级注解控制
                )
                .addFilterBefore((Filter) jwtSecurityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 使用allowedOriginPatterns允许所有源，同时保持allowCredentials=true
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // 允许发送凭据
        configuration.setExposedHeaders(Arrays.asList("Authorization")); // 允许前端访问的响应头
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}













