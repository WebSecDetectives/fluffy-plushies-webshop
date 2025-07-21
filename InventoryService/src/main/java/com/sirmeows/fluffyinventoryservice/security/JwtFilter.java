package com.sirmeows.fluffyinventoryservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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

        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            var token = authorizationHeader.substring(BEARER_PREFIX.length());

            var parsedToken = jwtUtil.parseToken(token);

            setUserDetails(parsedToken, request);
        }
    }

    private void setUserDetails(ParsedJwtToken token, HttpServletRequest request) {
        try {

            var username = token.getUsername();

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Take user details from signed JWT token to pass to Spring Security Context
                // Alternative could be to do a request to identityservice to get user information that way
                var userDetails = User.builder()
                        .username(token.getUsername())
                        .password("") // No password needed as we're authenticating via JWT
                        .authorities(AuthorityUtils.createAuthorityList(token.getRole().name()))
                        .build();
                setAuthentication(userDetails, request);
            }
        } catch (Exception e) {
            log.error("Failed to process JWT token", e);
        }
    }

    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null, // credentials can be null as we're using JWT
                userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
