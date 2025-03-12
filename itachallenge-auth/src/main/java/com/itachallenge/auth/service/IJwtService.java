package com.itachallenge.auth.service;

public interface IJwtService {
    String generateToken(String username, String role, String uuid);
}
