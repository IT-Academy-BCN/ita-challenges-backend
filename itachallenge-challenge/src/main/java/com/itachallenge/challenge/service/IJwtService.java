package com.itachallenge.challenge.service;


public interface IJwtService {
    String getUserUuIdFromAuthenticationHeader(String authHeader);
}
