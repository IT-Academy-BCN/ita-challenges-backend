package com.itachallenge.challenge.service;

import com.itachallenge.challenge.exception.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
    }

    @Test
    void getUserUuIdFromAuthenticationHeader_WhenValidHeader_ReturnUUID() {
        String userId = "userId";
        String base64EncryptedInfo = "eyJzdWIiOiJ0ZXN0VXNlciIsInJvbGUiOiJBRE1JTiIsInV1aWQiOiJ1c2VySWQiLCJpYXQiOjE3NDE2MTM2MTIsImV4cCI6MTc0MTY0OTYxMn0=";

        String authHeaderConstructor = "Bearer %s.%s.%s";
        String authHeader = String.format(authHeaderConstructor, "Anything", base64EncryptedInfo, "Anything");

        assertEquals(userId, jwtService.getUserUuIdFromAuthenticationHeader(authHeader));
    }

    @Test
    void getUserUuIdFromAuthenticationHeader_WhenInvalidHeader_ReturnNull() {
        String base64EncryptedInfo = "BadToken";

        String authHeaderConstructor = "Bearer %s.%s.%s";
        String authHeader = String.format(authHeaderConstructor, "Anything", base64EncryptedInfo, "Anything");

        JwtException exception = assertThrows(JwtException.class, () ->
                jwtService.getUserUuIdFromAuthenticationHeader(authHeader));

        assertEquals("Invalid Authorization header content", exception.getMessage());
    }

    @Test
    void getUserUuIdFromAuthenticationHeader_WhenNullHeader_ReturnException() {

        JwtException exception = assertThrows(JwtException.class, () ->
                jwtService.getUserUuIdFromAuthenticationHeader(null));

        assertEquals("Missing or bad formatted Authorization header", exception.getMessage());
    }

    @Test
    void getUserUuIdFromAuthenticationHeader_WhenBadFormattedHeader_ReturnNull() {
        String base64EncryptedInfo = "GoodToken";

        String authHeaderConstructor = "TrampedBearer %s.%s.%s";
        String authHeader = String.format(authHeaderConstructor, "Anything", base64EncryptedInfo, "Anything");

        JwtException exception = assertThrows(JwtException.class, () ->
                jwtService.getUserUuIdFromAuthenticationHeader(authHeader));

        assertEquals("Missing or bad formatted Authorization header", exception.getMessage());
    }
}