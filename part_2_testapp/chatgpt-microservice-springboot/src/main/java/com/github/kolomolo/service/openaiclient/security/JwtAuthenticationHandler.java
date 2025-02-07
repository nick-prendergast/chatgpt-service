package com.github.kolomolo.service.openaiclient.security;

import com.github.kolomolo.service.openaiclient.exception.JwtAuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationHandler {
    private final JwtService jwtService;

    public void authenticateToken(String token) throws JwtAuthenticationException {
        try {
            log.debug("Attempting to authenticate token");
            String username = jwtService.validateTokenAndGetUsername(token);
            setAuthentication(username);
            log.debug("Successfully authenticated user: {}", username);
        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired", e);
            throw new JwtAuthenticationException("Token has expired", e);
        } catch (JwtException e) {
            log.warn("JWT token validation failed", e);
            throw new JwtAuthenticationException("Invalid token", e);
        }
    }

    private void setAuthentication(String username) {
        if (username == null) {
            log.error("Username cannot be null");
            throw new JwtAuthenticationException("Username cannot be null");
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Set authentication in SecurityContext for user: {}", username);
    }

    public void handleAuthenticationFailure(HttpServletResponse response, String message) throws IOException {
        log.warn("Authentication failed: {}", message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        writeJsonError(response, message);
    }

    public void handleUnexpectedError(HttpServletResponse response, Exception e) throws IOException {
        log.error("Unexpected authentication error", e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        writeJsonError(response, "Authentication failed");
    }

    private void writeJsonError(HttpServletResponse response, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
        response.flushBuffer();
        log.debug("Wrote error response: {}", message);
    }
}