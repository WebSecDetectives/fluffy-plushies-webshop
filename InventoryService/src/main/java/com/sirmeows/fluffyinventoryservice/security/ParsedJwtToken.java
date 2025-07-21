package com.sirmeows.fluffyinventoryservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParsedJwtToken {

    private JwsHeader jwsHeader;
    private Claims claims;

    public Date getExpiration() {
        return claims.getExpiration();
    }

    public UUID getSubject() {
        return UUID.fromString(claims.getSubject());
    }

    public Role getRole() {
        var role = claims.get("role", String.class);
        return Role.valueOf(role);
    }

    public String getUsername() {
        return claims.get("username", String.class);
    }

    private boolean isExpiredToken() {
        return getExpiration().before(new Date());
    }
}