package com.itachallenge.auth.service;

import io.jsonwebtoken.Claims;

public interface IAuthJwtFacade {

    String generateToken(String username, String role, String uuid);
    void validateToken(String token);
    Claims extractAllClaims(String token);
    String extractBearerToken(String authHeader);

}
