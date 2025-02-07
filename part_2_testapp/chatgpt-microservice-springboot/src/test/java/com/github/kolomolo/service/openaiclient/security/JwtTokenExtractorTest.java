package com.github.kolomolo.service.openaiclient.security;

import com.github.kolomolo.service.openaiclient.security.jwt.JwtTokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenExtractorTest {
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private JwtTokenExtractor tokenExtractor;

    @Test
    void extractToken_WithValidHeader_ShouldReturnToken() {
        // Given
        String token = "validToken";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        // When
        String result = tokenExtractor.extractToken(request);

        // Then
        assertEquals(token, result);
    }

    @Test
    void extractToken_WithMissingHeader_ShouldReturnNull() {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        String result = tokenExtractor.extractToken(request);

        // Then
        assertNull(result);
    }

    @Test
    void extractToken_WithInvalidPrefix_ShouldReturnNull() {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Basic token123");

        // When
        String result = tokenExtractor.extractToken(request);

        // Then
        assertNull(result);
    }
}