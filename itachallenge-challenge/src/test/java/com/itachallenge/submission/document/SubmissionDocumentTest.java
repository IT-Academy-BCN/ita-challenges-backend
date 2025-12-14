package com.itachallenge.submission.document;

import com.itachallenge.submission.enums.SubmissionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SubmissionDocumentTest {

    private SubmissionDocument submissionDocumentAllArgs;
    private SubmissionDocument submissionDocumentNoArgs;

    private final String submissionIdText = "c4feec44-ac54-4e99-852b-9ba56c47e56f";
    private final UUID submissionId = UUID.fromString(submissionIdText);

    private final String userIdText = "c4feec44-ac54-4e99-852b-9ba56c479ba5";
    private final UUID userId = UUID.fromString(userIdText);

    private final String challengeIdText = "c4feec44-ac54-4e99-852b-9ba56c476c47";
    private final UUID challengeId = UUID.fromString(challengeIdText);

    private final String languageIdText = "c4feec44-ac54-4e99-852b-9ba56c47eec4";
    private final UUID languageId = UUID.fromString(languageIdText);
    private final SubmissionStatus submissionStatus = SubmissionStatus.IN_PROGRESS;

    private final String submissionText = "Hello World!!";

    @BeforeEach
    void setUp() {
        submissionDocumentAllArgs = new SubmissionDocument(submissionId, userId, challengeId, languageId, submissionStatus, submissionText);
        submissionDocumentNoArgs = new SubmissionDocument();
    }

    @Test
    void shouldCreateDocumentWithAllArgsConstructor() {

        assertEquals(UUID.fromString(submissionIdText), submissionDocumentAllArgs.getSubmissionId());
        assertEquals(UUID.fromString(userIdText), submissionDocumentAllArgs.getUserId());
        assertEquals(SubmissionStatus.IN_PROGRESS, submissionDocumentAllArgs.getStatus());
    }

    @Test
    void shouldBuildDocumentCorrectly() {

        SubmissionDocument doc = SubmissionDocument.builder()
                .submissionId(submissionId)
                .userId(userId)
                .challengeId(challengeId)
                .languageId(languageId)
                .status(submissionStatus)
                .submissionText(submissionText)
                .build();

        assertEquals(UUID.fromString(submissionIdText), doc.getSubmissionId());
        assertEquals(UUID.fromString(userIdText), doc.getUserId());
        assertEquals(UUID.fromString(challengeIdText), doc.getChallengeId());
        assertEquals(UUID.fromString(languageIdText), doc.getLanguageId());
        assertEquals(SubmissionStatus.IN_PROGRESS, doc.getStatus());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        submissionDocumentAllArgs.setSubmissionId(userId);

        assertEquals(UUID.fromString(userIdText), submissionDocumentAllArgs.getSubmissionId());
    }

    @Test
    void shouldReturnUuidCorrectly() {
        assertEquals(UUID.fromString(submissionIdText), submissionDocumentAllArgs.getSubmissionId());
    }

    @Test
    void shouldReturnUserUuidCorrectly() {
        assertEquals(UUID.fromString(userIdText), submissionDocumentAllArgs.getUserId());
    }

    @Test
    void shouldReturnChallengeUuidCorrectly() {
        assertEquals(UUID.fromString(challengeIdText), submissionDocumentAllArgs.getChallengeId());
    }

    @Test
    void shouldReturnLanguageUuidCorrectly() {
        assertEquals(UUID.fromString(languageIdText), submissionDocumentAllArgs.getLanguageId());
    }

    @Test
    void shouldReturnStatusCorrectly() {
        assertEquals(SubmissionStatus.IN_PROGRESS, submissionDocumentAllArgs.getStatus());
    }

    @Test
    void shouldReturnSubmissionAttemptDocumentCorrectly() {
        assertEquals(submissionText, submissionDocumentAllArgs.getSubmissionText());
    }

    @Test
    void shouldInstantiateDocumentWithNoArgsConstructor() {
        assertNotNull(submissionDocumentNoArgs);
    }
}
