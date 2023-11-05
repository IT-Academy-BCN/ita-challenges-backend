package com.itachallenge.user.dtos;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OneSolutionUserDtoTest {

    @Test
    public void testGettersAndSetters() {
        UUID challengeId = UUID.randomUUID();
        UUID languageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String solutionText = "Ejemplo de solución";
        int score = 42;

        OneSolutionUserDto dto = new OneSolutionUserDto();
        dto.setChallengeId(challengeId);
        dto.setLanguageId(languageId);
        dto.setUserId(userId);
        dto.setSolutionText(solutionText);
        dto.setScore(score);

        assertEquals(challengeId, dto.getChallengeId());
        assertEquals(languageId, dto.getLanguageId());
        assertEquals(userId, dto.getUserId());
        assertEquals(solutionText, dto.getSolutionText());
        assertEquals(score, dto.getScore());
    }

    @Test
    public void testNullValues() {
        OneSolutionUserDto dto = OneSolutionUserDto.builder()
                .challengeId(null)
                .languageId(null)
                .userId(null)
                .solutionText(null)
                .build();

        assertNull(dto.getChallengeId());
        assertNull(dto.getLanguageId());
        assertNull(dto.getUserId());
        assertNull(dto.getSolutionText());
    }

}