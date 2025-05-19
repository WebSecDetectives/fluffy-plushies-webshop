package com.dlshomies.fluffyplushies.domain;

import com.dlshomies.fluffyplushies.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

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
        return claims.get("role", Role.class);
    }

    public String getUsername() {
        return claims.get("username", String.class);
    }

    private boolean isExpiredToken() {
        return getExpiration().before(new Date());
    }
}
