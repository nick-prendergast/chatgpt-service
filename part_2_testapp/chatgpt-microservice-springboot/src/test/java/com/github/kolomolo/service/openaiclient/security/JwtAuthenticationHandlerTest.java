package com.github.kolomolo.service.openaiclient.security;

import com.github.kolomolo.service.openaiclient.security.jwt.JwtAuthenticationHandler;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationHandlerTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private HttpServletResponse response;
    @Mock
    private PrintWriter writer;

    @InjectMocks
    private JwtAuthenticationHandler authHandler;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticateToken_WithValidToken_ShouldSetAuthentication() {
        // Given
        String validToken = "validToken";
        String username = "testuser";
        when(jwtService.validateTokenAndGetUsername(validToken)).thenReturn(username);

        // When
        authHandler.authenticateToken(validToken);

        // Then
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(username, auth.getPrincipal());
    }

    @Test
    void handleAuthenticationFailure_ShouldSetProperResponse() throws IOException {
        // Given
        String errorMessage = "Auth failed";
        when(response.getWriter()).thenReturn(writer);

        // When
        authHandler.handleAuthenticationFailure(response, errorMessage);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(writer).write(String.format("{\"error\": \"%s\"}", errorMessage));
    }
}
