package com.itachallenge.submission.document;

import com.itachallenge.submission.enums.ChallengeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserSubmissionDocumentTest {

    private UserSubmissionDocument userSubmissionDocumentAllArgs;
    private UserSubmissionDocument userSubmissionDocumentNoArgs;

    private SubmissionAttemptDocument submissionAttemptDocumentNoArgs;

    private final String uuidText = "c4feec44-ac54-4e99-852b-9ba56c47e56f";
    private final UUID uuid = UUID.fromString(uuidText);

    private final String userUuidText = "c4feec44-ac54-4e99-852b-9ba56c479ba5";
    private final UUID userUuid = UUID.fromString(userUuidText);

    private final String challengeUuidText = "c4feec44-ac54-4e99-852b-9ba56c476c47";
    private final UUID challengeUuid = UUID.fromString(challengeUuidText);

    private final String languageUuidText = "c4feec44-ac54-4e99-852b-9ba56c47eec4";
    private final UUID languageUuid = UUID.fromString(languageUuidText);
    private final ChallengeStatus userChallengeStatus = ChallengeStatus.IN_PROGRESS;

    @BeforeEach
    void setUp() {
        submissionAttemptDocumentNoArgs = new SubmissionAttemptDocument();
        userSubmissionDocumentAllArgs = new UserSubmissionDocument(uuid, userUuid, challengeUuid, languageUuid, userChallengeStatus, submissionAttemptDocumentNoArgs);
        userSubmissionDocumentNoArgs = new UserSubmissionDocument();
    }

    @Test
    void shouldCreateDocumentWithAllArgsConstructor() {

        assertEquals(UUID.fromString(uuidText), userSubmissionDocumentAllArgs.getSubmissionId());
        assertEquals(UUID.fromString(userUuidText), userSubmissionDocumentAllArgs.getUserId());
        assertEquals(ChallengeStatus.IN_PROGRESS, userSubmissionDocumentAllArgs.getStatus());
    }

    @Test
    void shouldBuildDocumentCorrectly() {

        UserSubmissionDocument doc = UserSubmissionDocument.builder()
                .submissionId(uuid)
                .userId(userUuid)
                .challengeId(challengeUuid)
                .languageId(languageUuid)
                .status(userChallengeStatus)
                .submissionAttemptDocument(submissionAttemptDocumentNoArgs)
                .build();

        assertEquals(UUID.fromString(uuidText), doc.getSubmissionId());
        assertEquals(UUID.fromString(userUuidText), doc.getUserId());
        assertEquals(UUID.fromString(challengeUuidText), doc.getChallengeId());
        assertEquals(UUID.fromString(languageUuidText), doc.getLanguageId());
        assertEquals(ChallengeStatus.IN_PROGRESS, doc.getStatus());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        userSubmissionDocumentAllArgs.setSubmissionId(userUuid);

        assertEquals(UUID.fromString(userUuidText), userSubmissionDocumentAllArgs.getSubmissionId());
    }

    @Test
    void shouldReturnUuidCorrectly() {
        assertEquals(UUID.fromString(uuidText), userSubmissionDocumentAllArgs.getSubmissionId());
    }

    @Test
    void shouldReturnUserUuidCorrectly() {
        assertEquals(UUID.fromString(userUuidText), userSubmissionDocumentAllArgs.getUserId());
    }

    @Test
    void shouldReturnChallengeUuidCorrectly() {
        assertEquals(UUID.fromString(challengeUuidText), userSubmissionDocumentAllArgs.getChallengeId());
    }

    @Test
    void shouldReturnLanguageUuidCorrectly() {
        assertEquals(UUID.fromString(languageUuidText), userSubmissionDocumentAllArgs.getLanguageId());
    }

    @Test
    void shouldReturnStatusCorrectly() {
        assertEquals(ChallengeStatus.IN_PROGRESS, userSubmissionDocumentAllArgs.getStatus());
    }

    @Test
    void shouldReturnSubmissionAttemptDocumentCorrectly() {
        assertEquals(submissionAttemptDocumentNoArgs, userSubmissionDocumentAllArgs.getSubmissionAttemptDocument());
    }

    @Test
    void shouldInstantiateDocumentWithNoArgsConstructor() {
        assertNotNull(userSubmissionDocumentNoArgs);
    }
}
