package com.itachallenge.submission.document;

import com.itachallenge.submission.enums.SubmissionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SubmissionDocumentTest {

    private SubmissionDocument submissionDocumentAllArgs;
    private SubmissionDocument submissionDocumentNoArgs;

    private final String uuidText = "c4feec44-ac54-4e99-852b-9ba56c47e56f";
    private final UUID uuid = UUID.fromString(uuidText);

    private final String userUuidText = "c4feec44-ac54-4e99-852b-9ba56c479ba5";
    private final UUID userUuid = UUID.fromString(userUuidText);

    private final String challengeUuidText = "c4feec44-ac54-4e99-852b-9ba56c476c47";
    private final UUID challengeUuid = UUID.fromString(challengeUuidText);

    private final String languageUuidText = "c4feec44-ac54-4e99-852b-9ba56c47eec4";
    private final UUID languageUuid = UUID.fromString(languageUuidText);
    private final SubmissionStatus submissionStatus = SubmissionStatus.IN_PROGRESS;

    private final String submissionText = "Hello World!!";

    @BeforeEach
    void setUp() {
        submissionDocumentAllArgs = new SubmissionDocument(uuid, userUuid, challengeUuid, languageUuid, submissionStatus, submissionText);
        submissionDocumentNoArgs = new SubmissionDocument();
    }

    @Test
    void shouldCreateDocumentWithAllArgsConstructor() {

        assertEquals(UUID.fromString(uuidText), submissionDocumentAllArgs.getSubmissionId());
        assertEquals(UUID.fromString(userUuidText), submissionDocumentAllArgs.getUserId());
        assertEquals(SubmissionStatus.IN_PROGRESS, submissionDocumentAllArgs.getStatus());
    }

    @Test
    void shouldBuildDocumentCorrectly() {

        SubmissionDocument doc = SubmissionDocument.builder()
                .submissionId(uuid)
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(submissionStatus)
                .submissionText(submissionText)
                .build();

        assertEquals(UUID.fromString(uuidText), doc.getSubmissionId());
        assertEquals(UUID.fromString(userUuidText), doc.getUserId());
        assertEquals(UUID.fromString(challengeUuidText), doc.getChallengeId());
        assertEquals(UUID.fromString(languageUuidText), doc.getLanguageId());
        assertEquals(SubmissionStatus.IN_PROGRESS, doc.getStatus());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        submissionDocumentAllArgs.setSubmissionId(userUuid);

        assertEquals(UUID.fromString(userUuidText), submissionDocumentAllArgs.getSubmissionId());
    }

    @Test
    void shouldReturnUuidCorrectly() {
        assertEquals(UUID.fromString(uuidText), submissionDocumentAllArgs.getSubmissionId());
    }

    @Test
    void shouldReturnUserUuidCorrectly() {
        assertEquals(UUID.fromString(userUuidText), submissionDocumentAllArgs.getUserId());
    }

    @Test
    void shouldReturnChallengeUuidCorrectly() {
        assertEquals(UUID.fromString(challengeUuidText), submissionDocumentAllArgs.getChallengeId());
    }

    @Test
    void shouldReturnLanguageUuidCorrectly() {
        assertEquals(UUID.fromString(languageUuidText), submissionDocumentAllArgs.getLanguageId());
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
