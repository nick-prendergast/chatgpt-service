package com.github.kolomolo.service.openaiclient.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @Mock
    private JwtTokenExtractor tokenExtractor;
    @Mock
    private JwtAuthenticationHandler authHandler;
    @Mock
    private SecurityPathMatcher pathMatcher;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithExcludedPath_ShouldContinueFilterChain() throws Exception {
        // Given
        when(pathMatcher.shouldFilter("/")).thenReturn(false);
        when(request.getServletPath()).thenReturn("/");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(tokenExtractor, never()).extractToken(any());
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldAuthenticate() throws Exception {
        // Given
        String validToken = "validToken";
        when(pathMatcher.shouldFilter("/api/v1/endpoint")).thenReturn(true);
        when(request.getServletPath()).thenReturn("/api/v1/endpoint");
        when(tokenExtractor.extractToken(request)).thenReturn(validToken);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(authHandler).authenticateToken(validToken);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithMissingToken_ShouldHandleFailure() throws Exception {
        // Given
        when(pathMatcher.shouldFilter("/api/v1/endpoint")).thenReturn(true);
        when(request.getServletPath()).thenReturn("/api/v1/endpoint");
        when(tokenExtractor.extractToken(request)).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(authHandler).handleAuthenticationFailure(response, "Missing or invalid Authorization header");
        verify(filterChain, never()).doFilter(any(), any());
    }
}