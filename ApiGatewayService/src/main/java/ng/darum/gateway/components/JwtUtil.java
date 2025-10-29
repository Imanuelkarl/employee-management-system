package ng.darum.gateway.components;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import ng.darum.gateway.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    @Autowired
    JwtProperties properties;


    // ✅ Convert Base64 Secret Key to `Key` object
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(Base64.getEncoder().encodeToString(properties.getSecret().getBytes()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ✅ Generate JWT Token
    @SuppressWarnings("deprecation")
	public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + properties.getExpiration()))
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

