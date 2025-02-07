package com.github.kolomolo.service.openaiclient.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtTokenExtractor {
    private static final String BEARER_PREFIX = "Bearer ";

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            log.debug("No Authorization header found");
            return null;
        }

        if (!authHeader.startsWith(BEARER_PREFIX)) {
            log.debug("Authorization header does not start with Bearer prefix");
            return null;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        log.debug("Successfully extracted JWT token");
        return token;
    }
}