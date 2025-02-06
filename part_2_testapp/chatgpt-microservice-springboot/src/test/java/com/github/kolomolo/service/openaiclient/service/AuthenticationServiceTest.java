package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.exception.UnauthorizedException;
import com.github.kolomolo.service.openaiclient.model.request.AuthenticationRequest;
import com.github.kolomolo.service.openaiclient.model.response.AuthenticationResponse;
import com.github.kolomolo.service.openaiclient.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private static final String VALID_USERNAME = "testuser";
    private static final String VALID_PASSWORD = "testpass";
    private static final String TEST_TOKEN = "test.jwt.token";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authenticationService, "validUsername", VALID_USERNAME);
        ReflectionTestUtils.setField(authenticationService, "validPassword", VALID_PASSWORD);
    }

    @Test
    void authenticate_WithValidCredentials_ShouldReturnToken() {
        // Given
        AuthenticationRequest request = new AuthenticationRequest(VALID_USERNAME, VALID_PASSWORD);

        // Stub only when actually used in this specific test
        when(jwtService.generateToken(VALID_USERNAME)).thenReturn(TEST_TOKEN);

        // When
        AuthenticationResponse response = authenticationService.authenticate(request);

        // Then
        assertNotNull(response);
        assertEquals(TEST_TOKEN, response.token());
    }

    @Test
    void authenticate_WithInvalidUsername_ShouldThrowUnauthorizedException() {
        // Given
        AuthenticationRequest request = new AuthenticationRequest("wronguser", VALID_PASSWORD);

        // When & Then
        assertThrows(UnauthorizedException.class,
                () -> authenticationService.authenticate(request));
    }

    @Test
    void authenticate_WithInvalidPassword_ShouldThrowUnauthorizedException() {
        // Given
        AuthenticationRequest request = new AuthenticationRequest(VALID_USERNAME, "wrongpass");

        // When & Then
        assertThrows(UnauthorizedException.class,
                () -> authenticationService.authenticate(request));
    }

    @Test
    void authenticate_WithEmptyUsername_ShouldThrowUnauthorizedException() {
        // When & Then
        assertThrows(UnauthorizedException.class,
                () -> authenticationService.authenticate(new AuthenticationRequest("", VALID_PASSWORD)));
    }

    @Test
    void authenticate_WithEmptyPassword_ShouldThrowUnauthorizedException() {
        // When & Then
        assertThrows(UnauthorizedException.class,
                () -> authenticationService.authenticate(new AuthenticationRequest(VALID_USERNAME, "")));
    }

    @Test
    void authenticate_WithNullUsername_ShouldThrowUnauthorizedException() {
        // When & Then
        assertThrows(UnauthorizedException.class,
                () -> authenticationService.authenticate(new AuthenticationRequest(null, VALID_PASSWORD)));
    }

    @Test
    void authenticate_WithNullPassword_ShouldThrowUnauthorizedException() {
        // When & Then
        assertThrows(UnauthorizedException.class,
                () -> authenticationService.authenticate(new AuthenticationRequest(VALID_USERNAME, null)));
    }

}