package com.github.kolomolo.service.openaiclient.security;

import org.springframework.stereotype.Component;

@Component
public class SecurityPathMatcher {
    public boolean shouldFilter(String path) {
        return path.startsWith("/api/v1") && !path.startsWith("/api/v1/auth") && !path.startsWith("/WEB-INF");
    }
}
