package com.itachallenge.user.dto;
import com.itachallenge.user.dtos.SolutionScoreDto;
import com.itachallenge.user.enums.ChallengeStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SolutionScoreDtoTest {

    @Test
    void solutionScoreDtoShouldBeCreatedWithAllFields() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        UUID languageId = UUID.randomUUID();
        UUID solutionId = UUID.randomUUID();
        ChallengeStatus status = ChallengeStatus.SENT;
        int score = 100;
        String errors = "No errors";

        SolutionScoreDto dto = SolutionScoreDto.builder()
                .userId(userId)
                .challengeId(challengeId)
                .languageId(languageId)
                .solutionId(solutionId)
                .status(status)
                .score(score)
                .errors(errors)
                .build();

        assertEquals(userId, dto.getUserId());
        assertEquals(challengeId, dto.getChallengeId());
        assertEquals(languageId, dto.getLanguageId());
        assertEquals(solutionId, dto.getSolutionId());
        assertEquals(status, dto.getStatus());
        assertEquals(score, dto.getScore());
        assertEquals(errors, dto.getErrors());
    }

    @Test
    void solutionScoreDtoShouldHandleNullStatus() {
        SolutionScoreDto dto = new SolutionScoreDto();
        assertNull(dto.getStatus());
    }

    @Test
    void solutionScoreDtoShouldHandleEmptyErrors() {
        SolutionScoreDto dto = new SolutionScoreDto();
        assertNull(dto.getErrors());
    }

    @Test
    void solutionScoreDtoShouldHandleDefaultScore() {
        SolutionScoreDto dto = new SolutionScoreDto();
        assertEquals(0, dto.getScore());
    }

    @Test
    void solutionScoreDtoShouldHandleNullIds() {
        SolutionScoreDto dto = new SolutionScoreDto();
        assertNull(dto.getUserId());
        assertNull(dto.getChallengeId());
        assertNull(dto.getLanguageId());
        assertNull(dto.getSolutionId());
    }
}