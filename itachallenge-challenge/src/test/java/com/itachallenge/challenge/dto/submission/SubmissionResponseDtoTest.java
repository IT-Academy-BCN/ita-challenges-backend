package com.itachallenge.submission.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubmissionResponseDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void builderShouldCreateObjectCorrectly() {
        SubmissionResponseDto dto = SubmissionResponseDto.builder()
                .userId("123")
                .challengeId("456")
                .languageId("789")
                .submissionText("my submission text")
                .action("GIVE_UP")
                .build();

        assertEquals("123", dto.getUserId());
        assertEquals("456", dto.getChallengeId());
        assertEquals("789", dto.getLanguageId());
        assertEquals("my submission text", dto.getSubmissionText());
        assertEquals("GIVE_UP", dto.getAction());
    }

    @Test
    void jsonSerializationShouldUseJsonPropertyNames() throws JsonProcessingException {
        SubmissionResponseDto dto = SubmissionResponseDto.builder()
                .userId("111")
                .challengeId("222")
                .languageId("333")
                .submissionText("text here")
                .action("GIVE_UP")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertTrue(json.contains("\"uuid_user\":\"111\""));
        assertTrue(json.contains("\"uuid_challenge\":\"222\""));
        assertTrue(json.contains("\"uuid_language\":\"333\""));
        assertTrue(json.contains("\"submission_text\":\"text here\""));
        assertTrue(json.contains("\"action\":\"GIVE_UP\""));
    }
}
