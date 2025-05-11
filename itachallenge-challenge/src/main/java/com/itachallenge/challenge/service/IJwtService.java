package com.itachallenge.challenge.service;


public interface IJwtService {
    String getUserUuIdFromAuthenticationHeader(String authHeader);
    String getUserRoleFromAuthenticationHeader(String authHeader);
    boolean isAdmin(String authHeader);
}