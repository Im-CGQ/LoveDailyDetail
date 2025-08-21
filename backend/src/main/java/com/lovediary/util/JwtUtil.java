package com.lovediary.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        // 确保密钥长度至少为256位（32字节）用于HS256算法
        byte[] keyBytes = secret.getBytes();
        if (keyBytes.length < 32) {
            // 如果密钥太短，进行填充到32字节
            byte[] paddedKey = new byte[32];
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.length, 32));
            // 如果原始密钥不够长，用原始密钥填充剩余部分
            for (int i = keyBytes.length; i < 32; i++) {
                paddedKey[i] = keyBytes[i % keyBytes.length];
            }
            return Keys.hmacShaKeyFor(paddedKey);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }

    public String generateToken(String username, String role, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", userId);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (username.equals(tokenUsername) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    public Long getUserIdFromToken(String token) {
        System.out.println("JwtUtil - getUserIdFromToken called with token: " + (token != null ? token.substring(0, Math.min(50, token.length())) + "..." : "null"));
        final Claims claims = extractAllClaims(token);
        System.out.println("JwtUtil - All claims: " + claims);
        Object userIdObj = claims.get("userId");
        System.out.println("JwtUtil - userId from claims: " + userIdObj + " (type: " + (userIdObj != null ? userIdObj.getClass().getName() : "null") + ")");
        if (userIdObj != null) {
            if (userIdObj instanceof Integer) {
                Long result = ((Integer) userIdObj).longValue();
                System.out.println("JwtUtil - Converted Integer to Long: " + result);
                return result;
            } else if (userIdObj instanceof Long) {
                System.out.println("JwtUtil - Already Long: " + userIdObj);
                return (Long) userIdObj;
            }
        }
        System.out.println("JwtUtil - No userId found in token");
        return null;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
} 