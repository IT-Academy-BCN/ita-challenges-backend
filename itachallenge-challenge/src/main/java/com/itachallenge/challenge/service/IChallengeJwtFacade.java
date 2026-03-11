package com.itachallenge.challenge.service;

import io.jsonwebtoken.Claims;

public interface IChallengeJwtFacade {

    String getUserUuIdFromAuthenticationHeader(String authHeader);
    Claims extractAllClaims(String token);
    /**
     * Extracts the username (JWT subject) from the Authorization header. Returns null if header is missing or invalid.
     */
    String getUsernameFromAuthenticationHeader(String authHeader);
}
