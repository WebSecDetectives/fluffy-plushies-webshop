package com.dlshomies.fluffyplushies.security;

import com.dlshomies.fluffyplushies.domain.ParsedJwtToken;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JwtFilter is a Spring Security filter that provides JWT-based authentication by intercepting
 * and analyzing HTTP requests. It ensures that requests containing a valid JWT in the Authorization
 * header set up the authenticated user's context within the application.
 *
 * The filter processes each request to check for the presence of a Bearer token in the Authorization
 * header. If a token is present, it is parsed to validate its authenticity and extract user details.
 * Upon successful validation, the security context is populated with the user's authentication information.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Filters HTTP requests to perform JWT-based authentication.
     * This method intercepts requests to check for a valid JWT token in the Authorization header,
     * processes the token, and sets the authenticated user details in the SecurityContext if the token is valid.
     *
     * @param request  the HTTP request object that potentially carries the JWT token in its Authorization header
     * @param response the HTTP response object
     * @param filterChain the filter chain to pass the request and response to the next filter in the processing chain
     * @throws ServletException if an error occurs during the filtering process
     * @throws IOException if an I/O error occurs during the request or response processing
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        extractTokenFromHeader(request);

        filterChain.doFilter(request, response);
    }

    private void extractTokenFromHeader(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");

        if (!hasBearerToken(authorizationHeader)) {
            return;
        }

        var token = authorizationHeader.substring(BEARER_PREFIX.length());
        authenticateFromToken(token, request);
    }

    private boolean hasBearerToken(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX);
    }

    /**
     * Validates the token (signature + expiry, enforced by jjwt) and, if valid, populates the
     * security context. A malformed, expired, or otherwise invalid token is swallowed here so the
     * request continues unauthenticated: Spring Security then returns a clean 401 via
     * {@link com.dlshomies.fluffyplushies.api.RestAuthenticationEntryPoint} for protected endpoints,
     * while public endpoints still work. A bad token must never throw out of the filter.
     */
    private void authenticateFromToken(String token, HttpServletRequest request) {
        try {
            var parsedToken = jwtUtil.parseToken(token);
            setUserDetails(parsedToken, request);
        } catch (JwtException e) {
            log.warn("Rejected invalid JWT: {}", e.getClass().getSimpleName());
        }
    }

    private void setUserDetails(ParsedJwtToken token, HttpServletRequest request) {
        var username = token.getUsername();
        if (!shouldAuthenticate(username)) {
            return;
        }
        loadAndAuthenticate(username, request);
    }

    private boolean shouldAuthenticate(String username) {
        return username != null && SecurityContextHolder.getContext().getAuthentication() == null;
    }

    /**
     * Loads the user named in the (already validated) token and authenticates the request.
     * If the subject no longer maps to a user — e.g. the account was deleted after the token was
     * issued — the request continues unauthenticated (→ 401 for protected endpoints) rather than
     * surfacing a 500. Any other failure (e.g. a real datastore error) is left to propagate.
     */
    private void loadAndAuthenticate(String username, HttpServletRequest request) {
        try {
            var userDetails = userDetailsService.loadUserByUsername(username);
            setAuthentication(userDetails, request);
        } catch (UsernameNotFoundException e) {
            log.warn("JWT subject no longer maps to an existing user");
        }
    }

    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        var authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}