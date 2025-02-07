package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.exception.UnauthorizedException;
import com.github.kolomolo.service.openaiclient.model.request.AuthenticationRequest;
import com.github.kolomolo.service.openaiclient.model.response.AuthenticationResponse;
import com.github.kolomolo.service.openaiclient.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final JwtService jwtService;

    @Value("${jwt.username}")
    private String validUsername;

    @Value("${jwt.password}")
    private String validPassword;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Authentication attempt for user: {}", request.username());
        if (validUsername.equals(request.username()) &&
                validPassword.equals(request.password())) {
            String token = jwtService.generateToken(request.username());
            log.info("Authentication successful for user: {}", request.username());
            return new AuthenticationResponse(token);
        }
        log.warn("Authentication failed for user: {}", request.username());
        throw new UnauthorizedException("Invalid credentials");
    }
}