package com.wtu.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;


import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtil {
    public static String createJwt(String secretKey, long ttl, Map<String, Object> claims){
        // 指定签名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        // 过期时间
        long tokenTime = System.currentTimeMillis() + ttl;
        // 将字符串 secretKey 转换为 SecretKey 对象
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), signatureAlgorithm.getJcaName());

        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(tokenTime))
                .signWith(signatureAlgorithm, secretKeySpec);

        return jwtBuilder.compact();
    }

    public static Claims parseJwt(String secretKey, String token) {
        log.info("Parsing JWT token: {}", token);
        // 将字符串 secretKey 转换为 SecretKey 对象
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());

        return Jwts.parser()
                .setSigningKey(secretKeySpec)
                .parseClaimsJws(token)
                .getBody();
    }
}
