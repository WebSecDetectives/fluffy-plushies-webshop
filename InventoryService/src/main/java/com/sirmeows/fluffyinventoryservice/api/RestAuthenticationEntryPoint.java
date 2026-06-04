package com.sirmeows.fluffyinventoryservice.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Returns a generic JSON 401 when an unauthenticated caller hits a protected endpoint
 * (including when the JwtFilter rejected an expired/invalid token and left the request
 * unauthenticated). Kept generic so it never leaks why authentication failed.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest req,
                         HttpServletResponse res,
                         AuthenticationException authEx) throws IOException {
        res.setContentType("application/json");
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.getWriter().write("""
      {"error":"UNAUTHORIZED","message":"You must authenticate to access this resource"}
      """);
    }
}
