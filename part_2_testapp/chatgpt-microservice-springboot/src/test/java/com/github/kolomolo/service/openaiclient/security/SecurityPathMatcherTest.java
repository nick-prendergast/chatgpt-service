package com.github.kolomolo.service.openaiclient.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityPathMatcherTest {
    private SecurityPathMatcher pathMatcher;

    @BeforeEach
    void setUp() {
        pathMatcher = new SecurityPathMatcher();
    }

    @Test
    void shouldFilter_WithApiPath_ShouldReturnTrue() {
        assertTrue(pathMatcher.shouldFilter("/api/v1/endpoint"));
    }

    @Test
    void shouldFilter_WithAuthPath_ShouldReturnFalse() {
        assertFalse(pathMatcher.shouldFilter("/api/v1/auth/login"));
    }

    @Test
    void shouldFilter_WithRootPath_ShouldReturnFalse() {
        assertFalse(pathMatcher.shouldFilter("/"));
    }

    @Test
    void shouldFilter_WithWebInfPath_ShouldReturnFalse() {
        assertFalse(pathMatcher.shouldFilter("/WEB-INF/something"));
    }
}