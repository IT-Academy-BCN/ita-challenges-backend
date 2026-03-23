package com.itachallenge.challenge.service;

import com.itachallenge.jwtcore.service.IJwtService;
import com.itachallenge.common.exception.BadRequestException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChallengeJwtFacade implements IChallengeJwtFacade {

    private static final Logger log = LoggerFactory.getLogger(ChallengeJwtFacade.class);

    private final IJwtService jwtService;

    public ChallengeJwtFacade(IJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Claims extractAllClaims(String token) {
        return jwtService.extractAllClaims(token);
    }

    @Override
    public String getUserUuIdFromAuthenticationHeader(String authHeader) {
        return jwtService.getUserUuIdFromAuthenticationHeader(authHeader);
    }
    @Override
    public String getUsernameFromAuthenticationHeader(String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return null;
        }
        try {
            String token = jwtService.extractBearerToken(authHeader);
            return jwtService.extractAllClaims(token).getSubject();
        } catch (JwtException e) {
            log.warn("Could not extract username from Authorization header: {}", e.getMessage());
            // Header is present but token is invalid/expired => genuine error (distinguish from "missing header")
            throw new BadRequestException("Invalid or expired Authorization token.");
        }
    }
}
