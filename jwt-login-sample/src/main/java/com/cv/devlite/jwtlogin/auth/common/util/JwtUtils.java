package com.cv.devlite.jwtlogin.auth.common.util;


import com.cv.devlite.jwtlogin.auth.common.property.JwtProperties;
import com.cv.devlite.jwtlogin.auth.pojo.model.JwtUserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * JWT 工具类
 * - 支持对象封装（JwtUserInfoDTO）
 * - 支持签发、解析与校验
 *
 * @author: xutu
 * @since: 2025/10/10 15:59
 */
@Component
public class JwtUtils {

    private final JwtProperties jwtProperties;
    private Key key;

    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {
        if (jwtProperties.getSecret() == null || jwtProperties.getSecret().length() < 32) {
            throw new IllegalArgumentException("jwt.secret is missing or too short");
        }
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 基础生成 Token（兼容原方法）
     */
    public String generateToken(String username, Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration() * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 根据 JwtUserInfo 生成 Token
     */
    public String generateToken(JwtUserInfo userInfo) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userInfo.getUserId());
        claims.put("username", userInfo.getUsername());
        claims.put("tenantId", userInfo.getTenantId());
        claims.put("roles", userInfo.getRoles());
        return generateToken(userInfo.getUsername(), claims);
    }

    /**
     * 解析 Token
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 Token 中解析出 JwtUserInfoDTO
     */
    public JwtUserInfo parseUserInfo(String token) {
        Claims claims = parseToken(token);
        JwtUserInfo info = new JwtUserInfo();
        info.setUserId(claims.get("userId", Long.class));
        info.setUsername(claims.get("username", String.class));
        info.setTenantId(claims.get("tenantId", Long.class));
        info.setRoles(claims.get("roles", java.util.List.class));
        return info;
    }

    /**
     * 校验 Token 是否有效
     */
    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 获取 Token 剩余有效期（秒）
     */
    public long getRemainingTime(String token) {
        Claims claims = parseToken(token);
        Date expiration = claims.getExpiration();
        return (expiration.getTime() - System.currentTimeMillis()) / 1000;
    }

}
