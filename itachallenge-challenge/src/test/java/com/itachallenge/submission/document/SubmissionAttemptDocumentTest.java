package com.itachallenge.submission.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SubmissionAttemptDocumentTest {

    private SubmissionAttemptDocument submissionAttemptDocumentAllArgs;
    private SubmissionAttemptDocument submissionAttemptDocumentNoArgs;
    private final String uuid_text = "c4feec44-ac54-4e99-852b-9ba56c47e56f";
    private final UUID uuid = UUID.fromString(uuid_text);
    private final String submissionText = "Hello World!!";

    @BeforeEach
    void setUp() {
        submissionAttemptDocumentAllArgs = new SubmissionAttemptDocument(uuid, submissionText);
        submissionAttemptDocumentNoArgs = new SubmissionAttemptDocument();
    }

    @Test
    void shouldCreateDocumentUsingAllArgsConstructor() {
        assertEquals(UUID.fromString(uuid_text), submissionAttemptDocumentAllArgs.getUuid());
        assertEquals("Hello World!!", submissionAttemptDocumentAllArgs.getSubmissionText());
    }

    @Test
    void shouldBuildDocumentUsingBuilder() {
        SubmissionAttemptDocument doc = SubmissionAttemptDocument.builder()
                .uuid(uuid)
                .submissionText(submissionText)
                .build();

        assertEquals(UUID.fromString(uuid_text), doc.getUuid());
        assertEquals("Hello World!!", doc.getSubmissionText());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        submissionAttemptDocumentNoArgs.setUuid(uuid);
        submissionAttemptDocumentNoArgs.setSubmissionText(submissionText);

        assertEquals(UUID.fromString(uuid_text), submissionAttemptDocumentNoArgs.getUuid());
        assertEquals("Hello World!!", submissionAttemptDocumentNoArgs.getSubmissionText());
    }

    @Test
    void shouldReturnTrueWhenEqualsAndHashCodeMatch() {
        SubmissionAttemptDocument doc1 = SubmissionAttemptDocument.builder()
                .uuid(uuid)
                .submissionText(submissionText)
                .build();

        SubmissionAttemptDocument doc2 = SubmissionAttemptDocument.builder()
                .uuid(uuid)
                .submissionText(submissionText)
                .build();

        assertEquals(doc1, doc2);
        assertEquals(doc1.hashCode(), doc2.hashCode());
    }

    @Test
    void shouldReturnUuidCorrectly() {
        assertEquals(UUID.fromString(uuid_text), submissionAttemptDocumentAllArgs.getUuid());
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
