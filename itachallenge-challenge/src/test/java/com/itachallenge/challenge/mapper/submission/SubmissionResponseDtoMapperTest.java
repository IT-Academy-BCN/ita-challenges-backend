package com.itachallenge.challenge.mapper.submission;

import com.itachallenge.challenge.dto.submission.SubmissionResponseDto;
import com.itachallenge.submission.document.SubmissionDocument;
import com.itachallenge.submission.enums.SubmissionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SubmissionResponseDtoMapperTest {

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

    @Test
    @DisplayName("Test: Successful mapping from SubmissionDocument to DTO using Mockito")
    void testToDto_SuccessfulMapping() {
        SubmissionResponseDto resultDto = SubmissionResponseDtoMapper.toDto(document);

        assertNotNull(resultDto);
        assertEquals(userIdText, resultDto.getUserId(), "The userId should be mapped to String.");
        assertEquals(challengeIdText, resultDto.getChallengeId(), "The challengeId should be mapped to String.");
        assertEquals(languageIdText, resultDto.getLanguageId(), "The languageId should be mapped to String.");
        assertEquals(submissionStatusName, resultDto.getStatus(), "The status Enum should be mapped to its String name.");
        assertEquals(submissionText, resultDto.getSubmissionText(), "The submissionText should match.");
    }

    @Test
    @DisplayName("Test: Returns null when the input document is null")
    void testToDto_NullDocument_ReturnsNull() {
        assertNull(SubmissionResponseDtoMapper.toDto(null), "Mapping a null document should return null.");
    }
}
