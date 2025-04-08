package com.dlshomies.fluffyplushies.domain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParsedJwtToken {

    private JwsHeader jwsHeader;
    private Claims claims;

    public Date getExpiration() {
        return claims.getExpiration();
    }

    // Refers to the username
    public String getSubject() {
        return claims.getSubject();
    }

    public boolean isValidToken(UserDetails userDetails) {
        return userDetails.getUsername().equals(getSubject()) && !isExpiredToken();
    }

    private boolean isExpiredToken() {
        return getExpiration().before(new Date());
    }
}
