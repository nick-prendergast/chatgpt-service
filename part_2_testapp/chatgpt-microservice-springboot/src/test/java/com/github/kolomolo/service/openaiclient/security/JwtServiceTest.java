package com.github.kolomolo.service.openaiclient.security;

import com.github.kolomolo.service.openaiclient.security.jwt.JwtService;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.username}")
    private String testUsername;

    @Test
    void generateToken_ShouldCreateValidToken() {
        // When
        String token = jwtService.generateToken(testUsername);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());

        String username = jwtService.validateTokenAndGetUsername(token);
        assertEquals(testUsername, username);
    }

    @Test
    void validateTokenAndGetUsername_WithValidToken_ShouldReturnUsername() {
        // Given
        String token = jwtService.generateToken(testUsername);

        // When
        String username = jwtService.validateTokenAndGetUsername(token);

        // Then
        assertEquals(testUsername, username);
    }


    @Test
    void validateTokenAndGetUsername_WithInvalidToken_ShouldThrowException() {
        // Given
        String invalidToken = "invalid.token.string";

        // When & Then
        assertThrows(JwtException.class,
                () -> jwtService.validateTokenAndGetUsername(invalidToken));
    }

    @Test
    void validateTokenAndGetUsername_WithModifiedToken_ShouldThrowException() {
        // Given
        String token = jwtService.generateToken(testUsername);
        String modifiedToken = token + "invalidpart";

        // When & Then
        assertThrows(JwtException.class,
                () -> jwtService.validateTokenAndGetUsername(modifiedToken));
    }
}