package com.itachallenge.jwtcore.service;

import io.jsonwebtoken.Claims;
import javax.crypto.SecretKey;

public interface IJwtService {
    SecretKey getSigningKey();
    String generateToken(String username, String role, String uuid);
    void validateToken(String token);
    Claims extractAllClaims(String token);
    String extractBearerToken(String authHeader);
    String getUserUuIdFromAuthenticationHeader(String authHeader);

}