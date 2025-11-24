package com.itachallenge.submission.document;

import com.itachallenge.submission.enums.UserSubmissionAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserSubmissionDocumentTest {

    private UserSubmissionDocument userSubmissionDocumentAllArgs;
    private UserSubmissionDocument userSubmissionDocumentNoArgs;

    private SubmissionAttemptDocument submissionAttemptDocumentNoArgs;

    private final String uuid_text = "c4feec44-ac54-4e99-852b-9ba56c47e56f";
    private final UUID uuid = UUID.fromString(uuid_text);

    private final String user_uuid_text = "c4feec44-ac54-4e99-852b-9ba56c479ba5";
    private final UUID user_uuid = UUID.fromString(user_uuid_text);

    private final String challenge_uuid_text = "c4feec44-ac54-4e99-852b-9ba56c476c47";
    private final UUID challenge_uuid = UUID.fromString(challenge_uuid_text);

    private final String language_uuid_text = "c4feec44-ac54-4e99-852b-9ba56c47eec4";
    private final UUID language_uuid = UUID.fromString(language_uuid_text);

    private final UserSubmissionAction userSubmissionAction = UserSubmissionAction.SUBMIT;

    @BeforeEach
    void setUp() {
        submissionAttemptDocumentNoArgs = new SubmissionAttemptDocument();
        userSubmissionDocumentAllArgs = new UserSubmissionDocument(uuid, user_uuid, challenge_uuid, language_uuid, userSubmissionAction, submissionAttemptDocumentNoArgs);
        userSubmissionDocumentNoArgs = new UserSubmissionDocument();
    }

    @Test
    void shouldCreateDocumentWithAllArgsConstructor() {

        assertEquals(UUID.fromString(uuid_text), userSubmissionDocumentAllArgs.getUuid());
        assertEquals(UUID.fromString(user_uuid_text), userSubmissionDocumentAllArgs.getUserId());
        assertEquals(UserSubmissionAction.SUBMIT, userSubmissionDocumentAllArgs.getAction());
    }

    @Test
    void shouldBuildDocumentCorrectly() {

        UserSubmissionDocument doc = UserSubmissionDocument.builder()
                .uuid(uuid)
                .userId(user_uuid)
                .challengeId(challenge_uuid)
                .languageId(language_uuid)
                .action(userSubmissionAction)
                .submissionAttemptDocument(submissionAttemptDocumentNoArgs)
                .build();

        assertEquals(UUID.fromString(uuid_text), doc.getUuid());
        assertEquals(UUID.fromString(user_uuid_text), doc.getUserId());
        assertEquals(UUID.fromString(challenge_uuid_text), doc.getChallengeId());
        assertEquals(UUID.fromString(language_uuid_text), doc.getLanguageId());
        assertEquals(UserSubmissionAction.SUBMIT, doc.getAction());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {

        userSubmissionDocumentAllArgs.setUuid(user_uuid);

        assertEquals(UUID.fromString(user_uuid_text), userSubmissionDocumentAllArgs.getUuid());
    }

    @Test
    void shouldReturnUuidCorrectly() {
        assertEquals(UUID.fromString(uuid_text), userSubmissionDocumentAllArgs.getUuid());
    }

    @Test
    void shouldReturnUserUuidCorrectly() {
        assertEquals(UUID.fromString(user_uuid_text), userSubmissionDocumentAllArgs.getUserId());
    }

    @Test
    void shouldReturnChallengeUuidCorrectly() {
        assertEquals(UUID.fromString(challenge_uuid_text), userSubmissionDocumentAllArgs.getChallengeId());
    }

    @Test
    void shouldReturnLanguageUuidCorrectly() {
        assertEquals(UUID.fromString(language_uuid_text), userSubmissionDocumentAllArgs.getLanguageId());
    }

    @Test
    void shouldReturnStatusCorrectly() {
        assertEquals(UserSubmissionAction.SUBMIT, userSubmissionDocumentAllArgs.getAction());
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
