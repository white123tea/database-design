package org.example.vueback1.common;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码哈希加盐工具类（基于 Spring Security 内置 BCrypt，无需 jBCrypt 依赖）
 */
@Component // 可注入 Spring 容器，也可直接用 static 方法
public class PasswordUtil {

    // 单例 BCrypt 编码器（线程安全，无需重复创建）
    private static final BCryptPasswordEncoder BCRYPT_ENCODER = new BCryptPasswordEncoder();

    /**
     * 明文密码 → 哈希加盐后的密码（自动生成盐，无需手动处理）
     * @param rawPassword 用户输入的明文密码（如 "123456"）
     * @return 哈希后的密码（含盐，可直接存入数据库，长度固定 60 字符）
     */
    public static String encryptPassword(String rawPassword) {
        // BCrypt 自动生成随机盐，并将盐嵌入哈希结果中
        return BCRYPT_ENCODER.encode(rawPassword);
    }

    /**
     * 验证明文密码是否匹配数据库中的哈希密码
     * @param rawPassword 用户登录时输入的明文密码
     * @param encodedPassword 数据库中存储的哈希密码（含盐）
     * @return 匹配返回 true，不匹配返回 false
     */
    public static boolean matchPassword(String rawPassword, String encodedPassword) {
        // 自动从哈希密码中提取盐，与明文密码重新计算后比对
        return BCRYPT_ENCODER.matches(rawPassword, encodedPassword);
    }
}