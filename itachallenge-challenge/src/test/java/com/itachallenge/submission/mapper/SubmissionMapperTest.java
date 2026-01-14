package com.itachallenge.submission.mapper;

import com.itachallenge.challenge.dto.submission.SubmissionDto;
import com.itachallenge.submission.document.SubmissionDocument;
import com.itachallenge.submission.enums.SubmissionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SubmissionMapperTest {

    private static final String USER_ID_TEXT = "c4feec44-ac54-4e99-852b-9ba56c479ba5";
    private static final UUID USER_ID = UUID.fromString(USER_ID_TEXT);

    private static final String CHALLENGE_ID_TEXT = "c4feec44-ac54-4e99-852b-9ba56c476c47";
    private static final UUID CHALLENGE_ID = UUID.fromString(CHALLENGE_ID_TEXT);

    private static final String LANGUAGE_ID_TEXT = "c4feec44-ac54-4e99-852b-9ba56c47eec4";
    private static final UUID LANGUAGE_ID = UUID.fromString(LANGUAGE_ID_TEXT);

    private static final SubmissionStatus SUBMISSION_STATUS = SubmissionStatus.IN_PROGRESS;
    private static final String SUBMISSION_TEXT = "Hello World!!";

    private SubmissionDocument document;

    @BeforeEach
    void setUp() {
        document = mock(SubmissionDocument.class);
        when(document.getUserId()).thenReturn(USER_ID);
        when(document.getChallengeId()).thenReturn(CHALLENGE_ID);
        when(document.getLanguageId()).thenReturn(LANGUAGE_ID);
        when(document.getStatus()).thenReturn(SUBMISSION_STATUS);
        when(document.getSubmissionText()).thenReturn(SUBMISSION_TEXT);
    }

    @Test
    void toDto_shouldMapAllFields() {
        SubmissionDto dto = SubmissionMapper.toDto(document);

        assertEquals(USER_ID_TEXT, dto.getUserId());
        assertEquals(CHALLENGE_ID_TEXT, dto.getChallengeId());
        assertEquals(LANGUAGE_ID_TEXT, dto.getLanguageId());
        assertEquals(SUBMISSION_STATUS.name(), dto.getStatus());
        assertEquals(SUBMISSION_TEXT, dto.getSubmissionText());
    }

    @Test
    void toDto_nullDocument_shouldThrowNullPointerException() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> SubmissionMapper.toDto(null));
        assertEquals("SubmissionDocument cannot be null", ex.getMessage());
    }
}