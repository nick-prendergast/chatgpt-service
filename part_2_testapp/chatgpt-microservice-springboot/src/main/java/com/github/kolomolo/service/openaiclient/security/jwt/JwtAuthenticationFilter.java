package com.github.kolomolo.service.openaiclient.security.jwt;

import com.github.kolomolo.service.openaiclient.exception.JwtAuthenticationException;
import com.github.kolomolo.service.openaiclient.security.SecurityPathMatcher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenExtractor tokenExtractor;
    private final JwtAuthenticationHandler authHandler;
    private final SecurityPathMatcher pathMatcher;

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getServletPath();
        if (!pathMatcher.shouldFilter(path)) {
            log.debug("Skipping JWT authentication for path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Processing JWT authentication for path: {}", path);
        try {
            String token = tokenExtractor.extractToken(request);
            if (token == null) {
                log.warn("No JWT token found in request");
                authHandler.handleAuthenticationFailure(response, "Missing or invalid Authorization header");
                return;
            }

            try {
                authHandler.authenticateToken(token);
                log.debug("JWT authentication successful for path: {}", path);
                filterChain.doFilter(request, response);
            } catch (JwtAuthenticationException e) {
                log.warn("JWT authentication failed: {}", e.getMessage());
                authHandler.handleAuthenticationFailure(response, e.getMessage());
            }
        } catch (Exception e) {
            log.error("Unexpected error in JWT authentication", e);
            authHandler.handleUnexpectedError(response, e);
        }
    }

}
