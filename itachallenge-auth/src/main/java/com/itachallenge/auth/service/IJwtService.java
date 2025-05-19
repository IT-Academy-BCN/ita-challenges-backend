package com.itachallenge.auth.service;
import javax.crypto.SecretKey;

public interface IJwtService {
    String generateToken(String username, String role, String uuid);
    void validateToken(String token);
}
