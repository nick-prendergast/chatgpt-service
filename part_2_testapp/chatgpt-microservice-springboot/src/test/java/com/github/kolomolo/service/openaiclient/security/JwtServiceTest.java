package com.github.kolomolo.service.openaiclient.security;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET_KEY = "testsecretkeytestsecretkeytestsecretkeytestsecretkey";
    private static final String TEST_USERNAME = "testuser";
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // When
        String token = jwtService.generateToken(TEST_USERNAME);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());

        String username = jwtService.validateTokenAndGetUsername(token);
        assertEquals(TEST_USERNAME, username);
    }

    @Test
    void validateTokenAndGetUsername_WithValidToken_ShouldReturnUsername() {
        // Given
        String token = jwtService.generateToken(TEST_USERNAME);

        // When
        String username = jwtService.validateTokenAndGetUsername(token);

        // Then
        assertEquals(TEST_USERNAME, username);
    }

    @Test
    void validateTokenAndGetUsername_WithExpiredToken_ShouldThrowException() {
        // Given
        String expiredToken = Jwts.builder()
                .setSubject(TEST_USERNAME)
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(ReflectionTestUtils.invokeMethod(jwtService, "getSignInKey"))
                .compact();

        // When & Then
        assertThrows(RuntimeException.class,
                () -> jwtService.validateTokenAndGetUsername(expiredToken));
    }

    @Test
    void validateTokenAndGetUsername_WithInvalidToken_ShouldThrowException() {
        // Given
        String invalidToken = "invalid.token.string";

        // When & Then
        assertThrows(RuntimeException.class,
                () -> jwtService.validateTokenAndGetUsername(invalidToken));
    }

    @Test
    void validateTokenAndGetUsername_WithModifiedToken_ShouldThrowException() {
        // Given
        String token = jwtService.generateToken(TEST_USERNAME);
        String modifiedToken = token + "invalidpart";

        // When & Then
        assertThrows(RuntimeException.class,
                () -> jwtService.validateTokenAndGetUsername(modifiedToken));
    }
}