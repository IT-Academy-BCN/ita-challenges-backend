package com.itachallenge.score.dto;
import com.itachallenge.score.dto.zmq.ScoreResponseDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ScoreResponseDtoTest {

    @Test
    void scoreResponseDtoShouldBeCreatedWithAllFields() {
        UUID uuidChallenge = UUID.randomUUID();
        UUID uuidLanguage = UUID.randomUUID();
        String solutionText = "Sample solution text";
        int score = 100;
        String errors = "No errors";
        String compilationMessage = "Compilation successful";
        String expectedResult = "Expected output";

        ScoreResponseDto dto = ScoreResponseDto.builder()
                .uuidChallenge(uuidChallenge)
                .uuidLanguage(uuidLanguage)
                .solutionText(solutionText)
                .score(score)
                .errors(errors)
                .compilationMessage(compilationMessage)
                .expectedResult(expectedResult)
                .build();

        assertEquals(uuidChallenge, dto.getUuidChallenge());
        assertEquals(uuidLanguage, dto.getUuidLanguage());
        assertEquals(solutionText, dto.getSolutionText());
        assertEquals(score, dto.getScore());
        assertEquals(errors, dto.getErrors());
        assertEquals(compilationMessage, dto.getCompilationMessage());
        assertEquals(expectedResult, dto.getExpectedResult());
    }

    @Test
    void scoreResponseDtoShouldHandleNullFields() {
        ScoreResponseDto dto = new ScoreResponseDto();
        assertNull(dto.getUuidChallenge());
        assertNull(dto.getUuidLanguage());
        assertNull(dto.getSolutionText());
        assertEquals(0, dto.getScore());
        assertNull(dto.getErrors());
        assertNull(dto.getCompilationMessage());
        assertNull(dto.getExpectedResult());
    }

    @Test
    void scoreResponseDtoShouldHandleEmptyErrors() {
        ScoreResponseDto dto = new ScoreResponseDto();
        assertNull(dto.getErrors());
    }

    @Test
    void scoreResponseDtoShouldHandleDefaultScore() {
        ScoreResponseDto dto = new ScoreResponseDto();
        assertEquals(0, dto.getScore());
    }

    @Test
    void scoreResponseDtoShouldHandleEmptyCompilationMessage() {
        ScoreResponseDto dto = new ScoreResponseDto();
        assertNull(dto.getCompilationMessage());
    }

    @Test
    void scoreResponseDtoShouldHandleEmptyExpectedResult() {
        ScoreResponseDto dto = new ScoreResponseDto();
        assertNull(dto.getExpectedResult());
    }
}