package com.itachallenge.challenge.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("SomeStringReallyDifficultToGuessQWERTASDFGZXCVB");
    }

    @Test
    void extractUuid_WhenValidToken_ReturnUUID() {
        String userId = "userId";
        String token = jwtService.generateToken("testUser", "testRole", userId);

        assertEquals(userId, jwtService.extractUuid(token));
    }

    @Test
    void extractUuid_WhenInvalidToken_ReturnNull() {
        String userId = "userId";
        String tamperedToken = jwtService.generateToken("testUser", "testRole", userId) + "invalid";

        assertNull(jwtService.extractUuid(tamperedToken));
    }
}