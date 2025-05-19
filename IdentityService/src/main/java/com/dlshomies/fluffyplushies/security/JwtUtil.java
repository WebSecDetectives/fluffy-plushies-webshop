package com.dlshomies.fluffyplushies.security;

import com.dlshomies.fluffyplushies.domain.EncodedJwtToken;
import com.dlshomies.fluffyplushies.domain.ParsedJwtToken;
import com.dlshomies.fluffyplushies.entity.Role;
import com.dlshomies.fluffyplushies.entity.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * Utility for JWT generation and parsing.
 * Handles token creation with user details and validation for authentication.
 */
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

    public EncodedJwtToken generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        var encodedToken = Jwts.builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole().name())
                .claim("username", user.getUsername())
                .issuedAt(now)
                .notBefore(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
        return new EncodedJwtToken(encodedToken, expiryDate.getTime());
    }

    public ParsedJwtToken parseToken(String token) {
        var jwtParser = Jwts.parser()
                .verifyWith(key)
                .build();

        var claims = jwtParser.parseSignedClaims(token);

        return new ParsedJwtToken(claims.getHeader(), claims.getPayload());
    }
}
