package com.dlshomies.fluffyplushies.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest req,
                       HttpServletResponse res,
                       AccessDeniedException denied) throws IOException {
        res.setContentType("application/json");
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        res.getWriter().write("""
      {"error":"FORBIDDEN","message":"You do not have permission to access this resource"}
      """);
    }
}
