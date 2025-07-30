package com.example.loginsystem.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 用于生成和解析JWT token
 */
@Component
public class JwtUtil {

    /**
     * JWT密钥
     * 从配置文件中读取
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT过期时间
     * 从配置文件中读取，单位：毫秒
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成JWT token
     * @param username 用户名
     * @return 生成的JWT token
     */
    public String generateToken(String username) {
        // 创建载荷
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("created", new Date());

        // 生成token并返回
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 验证JWT token是否有效
     * @param token JWT token
     * @return token是否有效
     */
    public Boolean validateToken(String token) {
        try {
            // 解析token
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 解析出错，token无效
            return false;
        }
    }

    /**
     * 从token中获取用户名
     * @param token JWT token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            // 解析token获取载荷
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            // 返回用户名
            return claims.getSubject();
        } catch (Exception e) {
            // 解析出错，返回null
            return null;
        }
    }

    /**
     * 生成过期时间
     * @return 过期时间
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration);
    }
}