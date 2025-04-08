package com.dlshomies.fluffyplushies.security;

import com.dlshomies.fluffyplushies.domain.EncodedJwtToken;
import com.dlshomies.fluffyplushies.entity.Role;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long jwtExpirationMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expirationMs}") long jwtExpirationMs) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public EncodedJwtToken generateToken(String username, Role role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        var encodedToken = Jwts.builder()
                .subject(username)
                .claim("role", role.name())
                .issuedAt(now)
                .notBefore(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
        return new EncodedJwtToken(encodedToken, expiryDate.getTime());
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public Date getTokenExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    public boolean isValidToken(String token, UserDetails userDetails) {
        return userDetails.getUsername().equals(extractUsername(token)) && !isExpiredToken(token);
    }

    private boolean isExpiredToken(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (JwtException ex) {
            log.error("Token expired or invalid: {}", token, ex);
            return true;
        }
    }
    //TODO: create wrapper class that contains the parsed token payload and if needed token header

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException ex) {
            log.error("Failed to parse token: {}", token, ex);
            throw new IllegalArgumentException("Invalid token", ex);
        }
    }
}
