package com.zxl.xlforum.account.security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Component;

@Component
public class Argon2PasswordEncoder {

    private final Argon2 argon2;

    public Argon2PasswordEncoder() {
        // 使用默认参数创建Argon2实例
        this.argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    }


    public String encode(CharSequence rawPassword) {
        // 加密密码，会自动生成盐
        char[] password = rawPassword.toString().toCharArray();
        String hash;
        try {
            // 使用新方法哈希密码
            hash = argon2.hash(
                    10,          // 迭代次数 (iterations)
                    65536,      // 内存开销 (memory, 单位 KB)
                    2,          // 并行度 (parallelism)
                    password    // 密码用 char[] 传递
            );
        } finally {
            // 安全清理内存中的密码
            argon2.wipeArray(password);
        }
        return hash;
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // 验证密码
        char[] password = rawPassword.toString().toCharArray();
        boolean isMatch;
        try {
            isMatch = argon2.verify(encodedPassword, password);
        } finally {
            argon2.wipeArray(password);
        }
        return isMatch;
    }
}