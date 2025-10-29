package com.dailyadsmarketplace.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

import java.util.Base64;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private static final long EXPIRATION_TIME = 1000*24*60*60; 
    private static final String SECRET_KEY = "abcdefghijklmnopqrstuvxyz1234567890";


    // ✅ Convert Base64 Secret Key to `Key` object
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ✅ Generate JWT Token
    @SuppressWarnings("deprecation")
	public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) 
                .compact();
    }
    public String extractEmail(String token) {
        return validateToken(token).getSubject();
    }

    // ✅ Validate Token
    @SuppressWarnings("deprecation")
	public Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

