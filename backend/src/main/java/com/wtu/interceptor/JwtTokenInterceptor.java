package com.wtu.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wtu.properties.JwtProperties;
import com.wtu.result.Result;
import com.wtu.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {    // 获取请求路径
        //检测是否为动态方法，如果不是，直接放行
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        try {
            //检查token
            log.info("检验开始");
            String token = request.getHeader(jwtProperties.getTokenName());

            // 检查token是否为空
            if (token == null || token.isEmpty()) {
                log.error("Token为空");
                writeErrorResponse(response, "未提供认证令牌");
                return false;
            }
            try {
                Claims claims = JwtUtil.parseJwt(jwtProperties.getSecretKey(), token);
                log.info("检验token: {}", token);
                log.info("检验封装数据:{}", claims.toString());

                Object userId = claims.get("id");
                request.setAttribute("userId", userId); // 存入作用域
            } catch (Exception e) {
                log.error("Token解析失败: {}", e.getMessage());
                writeErrorResponse(response, "无效的认证令牌");
                return false;
            }

        } catch (Exception e) {
            log.error("认证失败: {}", e.getMessage());
            writeErrorResponse(response, "认证处理失败");
            return false;
        }
        return true;
    }

    /**
     * 使用Result类写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK); // 返回200状态码

        // 使用项目中已有的Result类创建错误响应
        Result<?> result = Result.error(message);

        // 将结果写入响应
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
