package com.itachallenge.challenge.dto.submission;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.mapper.submission.SubmissionResponseDtoMapper;
import com.itachallenge.submission.document.SubmissionDocument;
import com.itachallenge.submission.enums.SubmissionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SubmissionResponseDtoTest {

    private final String userIdText = "c4feec44-ac54-4e99-852b-9ba56c479ba5";
    private final UUID userId = UUID.fromString(userIdText);

    private final String challengeIdText = "c4feec44-ac54-4e99-852b-9ba56c476c47";
    private final UUID challengeId = UUID.fromString(challengeIdText);

    private final String languageIdText = "c4feec44-ac54-4e99-852b-9ba56c47eec4";
    private final UUID languageId = UUID.fromString(languageIdText);

    private final SubmissionStatus submissionStatus = SubmissionStatus.IN_PROGRESS;
    private final String submissionStatusName = submissionStatus.name();

    private final String submissionText = "Hello World!!";

    private SubmissionDocument document;

    @BeforeEach
    void setUp() {
        document = mock(SubmissionDocument.class);

        when(document.getUserId()).thenReturn(userId);
        when(document.getChallengeId()).thenReturn(challengeId);
        when(document.getLanguageId()).thenReturn(languageId);
        when(document.getStatus()).thenReturn(submissionStatus);
        when(document.getSubmissionText()).thenReturn(submissionText);
    }
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void mapperShouldCreateObjectCorrectly() {
        SubmissionResponseDto dto = SubmissionResponseDtoMapper.toDto(document);

        assertEquals(userIdText, dto.getUserId());
        assertEquals(challengeIdText, dto.getChallengeId());
        assertEquals(languageIdText, dto.getLanguageId());
        assertEquals(submissionStatusName, dto.getStatus());
        assertEquals(submissionText, dto.getSubmissionText());
    }

    @Test
    void jsonSerializationShouldUseJsonPropertyNames() throws JsonProcessingException {
        SubmissionResponseDto dto = SubmissionResponseDtoMapper.toDto(document);

        String json = objectMapper.writeValueAsString(dto);

        assertTrue(json.contains("\"uuid_user\":\"" + userIdText + "\""));
        assertTrue(json.contains("\"uuid_challenge\":\"" + challengeIdText + "\""));
        assertTrue(json.contains("\"uuid_language\":\"" + languageIdText + "\""));
        assertTrue(json.contains("\"status\":\"" + submissionStatusName + "\""));
        assertTrue(json.contains("\"submission_text\":\"" + submissionText + "\""));
    }
}
