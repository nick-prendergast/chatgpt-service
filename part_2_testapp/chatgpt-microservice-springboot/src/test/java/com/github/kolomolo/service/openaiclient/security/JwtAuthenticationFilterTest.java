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

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PrintWriter writer;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithNonApiPath_ShouldContinueFilterChain() throws Exception {
        // Given
        when(request.getServletPath()).thenReturn("/");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithMissingAuthorizationHeader_ShouldSetUnauthorizedStatus() throws Exception {
        // Given
        when(request.getServletPath()).thenReturn("/api/v1/some-endpoint");
        when(request.getHeader("Authorization")).thenReturn(null);
        when(response.getWriter()).thenReturn(writer);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).write("Missing or invalid Authorization header");
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldSetAuthentication() throws Exception {
        // Given
        String validToken = "validToken";
        String username = "testuser";
        when(request.getServletPath()).thenReturn("/api/v1/some-endpoint");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtService.validateTokenAndGetUsername(validToken)).thenReturn(username);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(username, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldSetUnauthorizedStatus() throws Exception {
        // Given
        String invalidToken = "invalidToken";
        when(request.getServletPath()).thenReturn("/api/v1/some-endpoint");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwtService.validateTokenAndGetUsername(invalidToken)).thenThrow(new RuntimeException("Invalid token"));
        when(response.getWriter()).thenReturn(writer);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).write("Invalid token");
    }
}