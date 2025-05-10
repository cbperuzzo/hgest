package com.lumem.hgest.model.Util;

import com.lumem.hgest.model.Role.RoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;
    private SecretKey key;

    public JwtUtil() {}

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        jwtSecret = null;
    }

    public String createToken(String name,String role,long uid){
        return Jwts.builder()
                .issuer("Hgest")
                .subject(name)
                .claim("uid",uid)
                .claim("role",role.toUpperCase())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                        .signWith(this.key).compact();
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    public String getSubjectName(String token) {
        try {
            return getAllClaims(token).getSubject();
        } catch (JwtException e) {
            return null;
        }
    }


    public Long getUID(String token) {
        try {
            return getAllClaims(token).get("uid", Long.class);
        } catch (JwtException e) {
            return null;
        }
    }


    public RoleEnum getRole(String token) {
        try {
             String roleName = getAllClaims(token).get("role", String.class);
             return RoleEnum.getRoleByName(roleName);
        } catch (JwtException e) {
            return null;
        }
    }

    public boolean isValid(String token) { //signature and not expired
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}

