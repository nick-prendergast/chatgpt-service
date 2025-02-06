package com.github.kolomolo.service.openaiclient.service;

import com.github.kolomolo.service.openaiclient.exception.UnauthorizedException;
import com.github.kolomolo.service.openaiclient.model.request.AuthenticationRequest;
import com.github.kolomolo.service.openaiclient.model.response.AuthenticationResponse;
import com.github.kolomolo.service.openaiclient.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;

    @Value("${jwt.username}")
    private String validUsername;

    @Value("${jwt.password}")
    private String validPassword;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        if (validUsername.equals(request.username()) &&
                validPassword.equals(request.password())) {
            String token = jwtService.generateToken(request.username());
            return new AuthenticationResponse(token);
        }

        throw new UnauthorizedException("Invalid credentials");
    }
}