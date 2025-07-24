package com.itachallenge.auth.service;

import com.itachallenge.jwtcore.service.IJwtService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

@Service
public class AuthJwtFacade implements IAuthJwtFacade{

    private final IJwtService jwtService;

    public AuthJwtFacade(IJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String generateToken(String username, String role, String uuid) {
        return jwtService.generateToken(username, role, uuid);
    }

    @Override
    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    @Override
    public Claims extractAllClaims(String token) {
        return jwtService.extractAllClaims(token);
    }

    @Override
    public String extractBearerToken(String authHeader) {
        return jwtService.extractBearerToken(authHeader);
    }
}
