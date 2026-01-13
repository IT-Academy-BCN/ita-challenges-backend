package com.itachallenge.challenge.dto.submission;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubmissionDtoTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String USER_ID_TEXT = "c4feec44-ac54-4e99-852b-9ba56c479ba5";
    private static final String CHALLENGE_ID_TEXT = "c4feec44-ac54-4e99-852b-9ba56c476c47";
    private static final String LANGUAGE_ID_TEXT = "c4feec44-ac54-4e99-852b-9ba56c47eec4";
    private static final String STATUS = "IN_PROGRESS";
    private static final String SUBMISSION_TEXT = "Hello World!!";

    @Test
    void builder_shouldCreateDtoCorrectly() {
        SubmissionDto dto = SubmissionDto.builder()
                .userId(USER_ID_TEXT)
                .challengeId(CHALLENGE_ID_TEXT)
                .languageId(LANGUAGE_ID_TEXT)
                .status(STATUS)
                .submissionText(SUBMISSION_TEXT)
                .build();

        assertEquals(USER_ID_TEXT, dto.getUserId());
        assertEquals(CHALLENGE_ID_TEXT, dto.getChallengeId());
        assertEquals(LANGUAGE_ID_TEXT, dto.getLanguageId());
        assertEquals(STATUS, dto.getStatus());
        assertEquals(SUBMISSION_TEXT, dto.getSubmissionText());
    }

    @Test
    void jsonSerialization_shouldUseExpectedJsonPropertyNames() throws JsonProcessingException {
        SubmissionDto dto = SubmissionDto.builder()
                .userId(USER_ID_TEXT)
                .challengeId(CHALLENGE_ID_TEXT)
                .languageId(LANGUAGE_ID_TEXT)
                .status(STATUS)
                .submissionText(SUBMISSION_TEXT)
                .build();

        String json = OBJECT_MAPPER.writeValueAsString(dto);
        JsonNode node = OBJECT_MAPPER.readTree(json);

        assertEquals(USER_ID_TEXT, node.get("uuid_user").asText());
        assertEquals(CHALLENGE_ID_TEXT, node.get("uuid_challenge").asText());
        assertEquals(LANGUAGE_ID_TEXT, node.get("uuid_language").asText());
        assertEquals(STATUS, node.get("status").asText());
        assertEquals(SUBMISSION_TEXT, node.get("submission_text").asText());
    }
}
