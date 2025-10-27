package com.zxl.xlforum.utils;

import java.security.SecureRandom;
import java.util.Base64;

public class JwtSecretGenerator {
    public static void main(String[] args) {
        // 生成64字节（512位）的强随机密钥
        byte[] secretBytes = new byte[64];
        new SecureRandom().nextBytes(secretBytes);

        // 转换为Base64格式（适合配置文件使用）
        String jwtSecret = Base64.getEncoder().encodeToString(secretBytes);

        System.out.println("Generated JWT Secret:");
        System.out.println(jwtSecret);
    }
}