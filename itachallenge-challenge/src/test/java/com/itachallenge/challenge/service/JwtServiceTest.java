package com.itachallenge.challenge.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JwtServiceTest {

    JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void extractUuid_WhenValidToken_ReturnUUID() {
        String userId = "userId";
        String base64EncryptedInfo = "eyJzdWIiOiJ0ZXN0VXNlciIsInJvbGUiOiJBRE1JTiIsInV1aWQiOiJ1c2VySWQiLCJpYXQiOjE3NDE2MTM2MTIsImV4cCI6MTc0MTY0OTYxMn0=";

        String tokenConstructor = "%s.%s.%s";
        String token = String.format(tokenConstructor, "Anything", base64EncryptedInfo, "Anything");

        assertEquals(userId, jwtService.extractUuid(token));
    }

    @Test
    void extractUuid_WhenInvalidToken_ReturnNull() {
        String base64EncryptedInfo = "BadToken";

        String tokenConstructor = "%s.%s.%s";
        String token = String.format(tokenConstructor, "Anything", base64EncryptedInfo, "Anything");

        assertNull(jwtService.extractUuid(token));
    }
}