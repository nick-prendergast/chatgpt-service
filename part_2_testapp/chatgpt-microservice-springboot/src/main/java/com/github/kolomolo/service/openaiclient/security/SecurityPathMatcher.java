package com.github.kolomolo.service.openaiclient.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SecurityPathMatcher {
    private static final Logger log = LoggerFactory.getLogger(SecurityPathMatcher.class);

    public boolean shouldFilter(String path) {
        boolean result = path.startsWith("/api/v1") && !path.startsWith("/api/v1/auth") && !path.startsWith("/WEB-INF");
        log.info("Path '{}' should be filtered: {}", path, result);
        return result;
    }
}
