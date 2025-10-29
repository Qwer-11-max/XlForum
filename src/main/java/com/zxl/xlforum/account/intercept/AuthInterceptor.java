package com.zxl.xlforum.account.intercept;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

@Component
@PropertySource("classpath:configs/JwtConfig.properties")
public class AuthInterceptor implements HandlerInterceptor {

    // 从配置中读取密钥，实际项目中应使用@Value注入
    @Value("${app.jwtSecret}")
    private String SECRET_KEY;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 只拦截特定路径
        String path = request.getRequestURI();
        if (!path.startsWith("/changePassword") && !path.startsWith("/signoff")) {
            return true;
        }

        // 从Header获取Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return false;
        }

        String token = authHeader.substring(7); // 去掉"Bearer "前缀

        try {
            // 生成签名密钥
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

            // 验证并解析Token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 将解析出的数据存入请求属性
            request.setAttribute("email", claims.getSubject());

            return true;

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: " + e.getMessage());
            return false;
        }
    }
}