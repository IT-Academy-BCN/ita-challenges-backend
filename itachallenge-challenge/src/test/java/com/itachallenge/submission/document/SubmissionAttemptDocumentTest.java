package com.itachallenge.submission.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SubmissionAttemptDocumentTest {

    private SubmissionAttemptDocument submissionAttemptDocumentAllArgs;
    private SubmissionAttemptDocument submissionAttemptDocumentNoArgs;
    private final String uuidText = "c4feec44-ac54-4e99-852b-9ba56c47e56f";
    private final UUID uuid = UUID.fromString(uuidText);
    private final String submissionText = "Hello World!!";

    @BeforeEach
    void setUp() {
        submissionAttemptDocumentAllArgs = new SubmissionAttemptDocument(uuid, submissionText);
        submissionAttemptDocumentNoArgs = new SubmissionAttemptDocument();
    }

    @Test
    void shouldCreateDocumentUsingAllArgsConstructor() {
        assertEquals(UUID.fromString(uuidText), submissionAttemptDocumentAllArgs.getSubmissionAttemptDocumentId());
        assertEquals("Hello World!!", submissionAttemptDocumentAllArgs.getSubmissionText());
    }

    @Test
    void shouldBuildDocumentUsingBuilder() {
        SubmissionAttemptDocument doc = SubmissionAttemptDocument.builder()
                .submissionAttemptDocumentId(uuid)
                .submissionText(submissionText)
                .build();

        assertEquals(UUID.fromString(uuidText), doc.getSubmissionAttemptDocumentId());
        assertEquals("Hello World!!", doc.getSubmissionText());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        submissionAttemptDocumentNoArgs.setSubmissionAttemptDocumentId(uuid);
        submissionAttemptDocumentNoArgs.setSubmissionText(submissionText);

        assertEquals(UUID.fromString(uuidText), submissionAttemptDocumentNoArgs.getSubmissionAttemptDocumentId());
        assertEquals("Hello World!!", submissionAttemptDocumentNoArgs.getSubmissionText());
    }

    @Test
    void shouldReturnUuidCorrectly() {
        assertEquals(UUID.fromString(uuidText), submissionAttemptDocumentAllArgs.getSubmissionAttemptDocumentId());
    }

    @Test
    void shouldReturnSubmissionTextCorrectly() {
        assertEquals("Hello World!!", submissionAttemptDocumentAllArgs.getSubmissionText());
    }

    @Test
    void shouldInstantiateDocumentWithNoArgsConstructor() {
        assertNotNull(submissionAttemptDocumentNoArgs);
    }
}
