package com.github.kolomolo.service.openaiclient.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
@Slf4j
public class JwtService {
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.expiration-ms}")
    private long expirationMs;
    @Value("${jwt.issuer}")
    private String issuer;

    public String generateToken(String username) {
        log.debug("Generating JWT token for user: {}", username);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setIssuer(issuer)
                .signWith(getSignInKey(), SIGNATURE_ALGORITHM)
                .compact();

        log.debug("Generated JWT token for user: {}, expires: {}", username, expiryDate);
        return token;
    }

    public String validateTokenAndGetUsername(String token) {
        try {
            log.debug("Validating JWT token");
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .requireIssuer(issuer)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            log.debug("Successfully validated JWT token for user: {}", username);
            return username;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (JwtException e) {
            log.warn("JWT token validation failed: {}", e.getMessage());
            throw e;
        }
    }


    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

}