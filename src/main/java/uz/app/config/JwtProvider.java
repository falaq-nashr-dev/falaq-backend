package uz.app.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import uz.app.entity.User;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${my_token.key}")
    private String key;

    @Value("${my_token.expire_time}")
    private Long expireTime;

    public String generateToken(User userDetails) {
        Date expiryDate = new Date(System.currentTimeMillis() + expireTime);
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setSubject(userDetails.getPhoneNumber())
                .setExpiration(expiryDate)
                .signWith(signKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key signKey() {
        if (key.length() < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 characters long");
        }
        return Keys.hmacShaKeyFor(key.getBytes());
    }

    public String getSubject(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired, please login again.");
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }
}
