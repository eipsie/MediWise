package com.wtu.utils;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * MD5加密工具类
 */
public class MD5Util {

    /**
     * 对字符串进行MD5加密
     * @param str 需要加密的字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String str) {
        if (str == null) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(str.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 校验密码是否匹配
     * @param inputPassword 输入的密码（明文）
     * @param encryptedPassword 数据库中存储的加密密码
     * @return 是否匹配
     */
    public static boolean matches(String inputPassword, String encryptedPassword) {
        if (inputPassword == null || encryptedPassword == null) {
            return false;
        }
        String encryptedInputPassword = encrypt(inputPassword);
        return encryptedInputPassword.equals(encryptedPassword);
    }
}
