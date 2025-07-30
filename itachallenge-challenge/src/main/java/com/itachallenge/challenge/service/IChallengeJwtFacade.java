package com.itachallenge.challenge.service;

import io.jsonwebtoken.Claims;

public interface IChallengeJwtFacade {

    String getUserUuIdFromAuthenticationHeader(String authHeader);
    Claims extractAllClaims(String token);


}
