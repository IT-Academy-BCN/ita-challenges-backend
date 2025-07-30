package com.itachallenge.challenge.service;

import com.itachallenge.jwtcore.service.IJwtService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

@Service
public class ChallengeJwtFacade implements IChallengeJwtFacade {

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
}
