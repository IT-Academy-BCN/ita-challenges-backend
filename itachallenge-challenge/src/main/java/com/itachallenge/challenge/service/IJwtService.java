package com.itachallenge.challenge.service;

import io.jsonwebtoken.Claims;

public interface IJwtService {
    String getUserUuIdFromAuthenticationHeader(String authHeader);
    String getUserRoleFromAuthenticationHeader(String authHeader);
    boolean isAdmin(String authHeader);
}