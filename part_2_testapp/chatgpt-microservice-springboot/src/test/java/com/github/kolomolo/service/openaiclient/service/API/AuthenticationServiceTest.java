package com.github.kolomolo.service.openaiclient.service.API;

import com.github.kolomolo.service.openaiclient.TestConstants;
import com.github.kolomolo.service.openaiclient.exception.UnauthorizedException;
import com.github.kolomolo.service.openaiclient.model.request.AuthenticationRequest;
import com.github.kolomolo.service.openaiclient.model.response.AuthenticationResponse;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtService;
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

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authenticationService, "validUsername",
                TestConstants.TestData.USERNAME);
        ReflectionTestUtils.setField(authenticationService, "validPassword",
                TestConstants.TestData.PASSWORD);
    }

    @Test
    void authenticate_WithValidCredentials_ShouldReturnToken() {
        AuthenticationRequest request = new AuthenticationRequest(
                TestConstants.TestData.USERNAME,
                TestConstants.TestData.PASSWORD
        );

        when(jwtService.generateToken(TestConstants.TestData.USERNAME))
                .thenReturn(TestConstants.JwtTokens.VALID);

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertNotNull(response);
        assertEquals(TestConstants.JwtTokens.VALID, response.token());
    }

    @Test
    void authenticate_WithInvalidUsername_ShouldThrowUnauthorizedException() {
        AuthenticationRequest request = new AuthenticationRequest("wronguser",
                TestConstants.TestData.PASSWORD);

        assertThrows(UnauthorizedException.class,
                () -> authenticationService.authenticate(request));
    }

    @Test
    void authenticate_WithInvalidPassword_ShouldThrowUnauthorizedException() {
        AuthenticationRequest request = new AuthenticationRequest(
                TestConstants.TestData.USERNAME, "wrongpass");

        assertThrows(UnauthorizedException.class,
                () -> authenticationService.authenticate(request));
    }

    @Test
    void authenticate_WithEmptyUsername_ShouldThrowUnauthorizedException() {
        assertThrows(UnauthorizedException.class,
                () -> authenticationService.authenticate(
                        new AuthenticationRequest("", TestConstants.TestData.PASSWORD)));
    }

    @Test
    void authenticate_WithEmptyPassword_ShouldThrowUnauthorizedException() {
        assertThrows(UnauthorizedException.class,
                () -> authenticationService.authenticate(
                        new AuthenticationRequest(TestConstants.TestData.USERNAME, "")));
    }

    @Test
    void authenticate_WithNullUsername_ShouldThrowUnauthorizedException() {
        assertThrows(UnauthorizedException.class,
                () -> authenticationService.authenticate(
                        new AuthenticationRequest(null, TestConstants.TestData.PASSWORD)));
    }

    @Test
    void authenticate_WithNullPassword_ShouldThrowUnauthorizedException() {
        assertThrows(UnauthorizedException.class,
                () -> authenticationService.authenticate(
                        new AuthenticationRequest(TestConstants.TestData.USERNAME, null)));
    }
}