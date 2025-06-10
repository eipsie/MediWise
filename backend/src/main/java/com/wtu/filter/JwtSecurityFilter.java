package com.wtu.filter;

import com.wtu.properties.JwtProperties;
import com.wtu.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtSecurityFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 获取请求头中的Authorization字段
        String token = request.getHeader(jwtProperties.getTokenName());

        // 如果token为空, 直接放行
        if(!StringUtils.hasText(token)){
            filterChain.doFilter(request, response);
            return;
        }

        // 处理Bearer前缀
        if(token.startsWith("Bearer ")){
            token = token.substring(7);
        }

        try{
            // 解析jwt
            Claims claims = JwtUtil.parseJwt(jwtProperties.getSecretKey(), token);

            // 获取用户信息
            String username = claims.get("username",String.class);
            String role = claims.getOrDefault("role", "DOCTOR").toString();

            if(StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null){
                // 创建权限列表(添加ROLE_前缀, Spring Security要求角色以ROLE_开头)
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + role)
                );

                // 创建认证对象
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);

                // 设置认证信息到SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // 设置用户ID到请求属性
                Object userId = claims.get("id");
                if(userId != null) {
                    request.setAttribute("userId", userId);
                }
            }
        } catch (Exception e) {
            log.error("JWT解析失败: {}", e.getMessage());
            // 如果解析失败, 可以选择抛出异常或记录日志
            // 这里不抛出异常, 直接放行
        }

        filterChain.doFilter(request, response);
    }
}















