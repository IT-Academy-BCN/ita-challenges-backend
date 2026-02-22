package com.itachallenge.challenge.dto.gamification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RankingResponseDtoTest {

    @Test
    @DisplayName("Should create RankingResponseDto with all args constructor")
    void shouldCreateRankingResponseDto_WithAllArgsConstructor() {
        RankingResponseDto dto = new RankingResponseDto("user1", 100);

        assertEquals("user1", dto.getUsername());
        assertEquals(100, dto.getPoints());
    }

    @Test
    @DisplayName("Should create RankingResponseDto with no args constructor")
    void shouldCreateRankingResponseDto_WithNoArgsConstructor() {
        RankingResponseDto dto = new RankingResponseDto();

        assertNull(dto.getUsername());
        assertEquals(0, dto.getPoints());
    }

    @Test
    @DisplayName("Should set and get username correctly")
    void shouldSetAndGetUsername() {
        RankingResponseDto dto = new RankingResponseDto();
        dto.setUsername("user1");

        assertEquals("user1", dto.getUsername());
    }

    @Test
    @DisplayName("Should set and get points correctly")
    void shouldSetAndGetPoints() {
        RankingResponseDto dto = new RankingResponseDto();
        dto.setPoints(100);

        assertEquals(100, dto.getPoints());
    }

    @Test
    @DisplayName("Should handle zero points")
    void shouldHandleZeroPoints() {
        RankingResponseDto dto = new RankingResponseDto("user1", 0);

        assertEquals(0, dto.getPoints());
    }
}