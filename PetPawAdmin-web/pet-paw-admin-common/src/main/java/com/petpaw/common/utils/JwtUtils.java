package com.petpaw.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
public class JwtUtils {

    private static final String SECRET = "petpaw-admin-secret-key-2024-12-25-very-long-secret-key-for-jwt-token-generation";
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * 生成JWT token
     */
    public static String generateToken(String username, Long userId) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + EXPIRE_TIME);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * 解析JWT token
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw new RuntimeException("Token已过期");
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的Token: {}", e.getMessage());
            throw new RuntimeException("不支持的Token");
        } catch (MalformedJwtException e) {
            log.warn("Token格式错误: {}", e.getMessage());
            throw new RuntimeException("Token格式错误");
        } catch (SignatureException e) {
            log.warn("Token签名验证失败: {}", e.getMessage());
            throw new RuntimeException("Token签名验证失败");
        } catch (IllegalArgumentException e) {
            log.warn("Token参数异常: {}", e.getMessage());
            throw new RuntimeException("Token参数异常");
        }
    }

    /**
     * 从token中获取用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 从token中获取用户ID
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 验证token是否过期
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 刷新token
     */
    public static String refreshToken(String token) {
        Claims claims = parseToken(token);
        String username = claims.getSubject();
        Long userId = claims.get("userId", Long.class);
        return generateToken(username, userId);
    }
}
