package com.dlshomies.fluffyplushies.api;

import com.dlshomies.fluffyplushies.entity.Role;
import com.dlshomies.fluffyplushies.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class JwtTestUtil {
    public static String expiredToken(User user, String jwtSecret) {
        var now = Instant.now();
        return signedToken(user.getId(), user.getUsername(), user.getRole(),
                now.minus(2, ChronoUnit.HOURS), now.minus(1, ChronoUnit.HOURS), jwtSecret);
    }

    public static String signedToken(UUID subject, String username, Role role, Instant issuedAt, Instant expiry, String jwtSecret) {
        return Jwts.builder()
                .subject(subject.toString())
                .claim("role", role.name())
                .claim("username", username)
                .issuedAt(Date.from(issuedAt))
                .notBefore(Date.from(issuedAt))
                .expiration(Date.from(expiry))
                .signWith(testSigningKey(jwtSecret))
                .compact();
    }

    public static SecretKey testSigningKey(String jwtSecret) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public static String tamperSignature(String token) {
        var lastChar = token.charAt(token.length() - 1);
        var replacement = lastChar == 'A' ? 'B' : 'A';
        return token.substring(0, token.length() - 1) + replacement;
    }
}
