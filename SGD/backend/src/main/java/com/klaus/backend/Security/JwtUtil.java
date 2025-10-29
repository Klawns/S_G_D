package com.klaus.backend.Security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {
    @Value("${app.jwt.secret}")
    private String jwtSecretString;

    private SecretKey jwtSecretKey;

    public static final String BEARER_ = "Bearer ";

    @PostConstruct
    public void init() {
        if (jwtSecretString == null || jwtSecretString.isEmpty()) {
            throw new IllegalStateException("JWT secret não foi configurada!");
        }
        this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecretString.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(jwtSecretKey)
                .compact();
    }

    public String getUserFromToken(String token) {
        return Jwts.parser().verifyWith(jwtSecretKey).build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(jwtSecretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String parseJwt(HttpServletRequest req) {
        String headerAuth = req.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith(BEARER_)) {
            return headerAuth.substring(BEARER_.length());
        }

        if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

}
