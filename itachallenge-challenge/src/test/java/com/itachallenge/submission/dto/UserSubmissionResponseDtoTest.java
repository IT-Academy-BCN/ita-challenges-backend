package com.itachallenge.submission.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSubmissionResponseDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void builderShouldCreateObjectCorrectly() {
        UserSubmissionResponseDto dto = UserSubmissionResponseDto.builder()
                .userId("123")
                .challengeId("456")
                .languageId("789")
                .solutionText("my solution text")
                .status("SUCCESS")
                .build();

        assertEquals("123", dto.getUserId());
        assertEquals("456", dto.getChallengeId());
        assertEquals("789", dto.getLanguageId());
        assertEquals("my solution text", dto.getSolutionText());
        assertEquals("SUCCESS", dto.getStatus());
    }

    @Test
    void jsonSerializationShouldUseJsonPropertyNames() throws JsonProcessingException {
        UserSubmissionResponseDto dto = UserSubmissionResponseDto.builder()
                .userId("111")
                .challengeId("222")
                .languageId("333")
                .solutionText("text here")
                .status("SUCCESS")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertTrue(json.contains("\"uuid_user\":\"111\""));
        assertTrue(json.contains("\"uuid_challenge\":\"222\""));
        assertTrue(json.contains("\"uuid_language\":\"333\""));
        assertTrue(json.contains("\"solution_text\":\"text here\""));
        assertTrue(json.contains("\"status\":\"SUCCESS\""));
    }
}
