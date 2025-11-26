package com.itachallenge.userinteraction.document.bookmark;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookmarkDocumentTest {

    private UUID uuid;
    private UUID userId;
    private UUID challengeId;
    private LocalDateTime createdAt;
    private BookmarkDocument bookmarkDocument;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        userId = UUID.randomUUID();
        challengeId = UUID.randomUUID();
        createdAt = LocalDateTime.now();

        bookmarkDocument = BookmarkDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();
    }

    @Test
    void bookmarkDocumentCreation_test() {
        Assertions.assertNotNull(bookmarkDocument);
        assertEquals(uuid, bookmarkDocument.getUuid());
        assertEquals(userId, bookmarkDocument.getUserId());
        assertEquals(challengeId, bookmarkDocument.getChallengeId());
        assertEquals(createdAt, bookmarkDocument.getCreatedAt());
    }

    @Test
    void noArgsConstructor_test() {
        BookmarkDocument emptyDoc = new BookmarkDocument();
        Assertions.assertNotNull(emptyDoc);
        assertNull(emptyDoc.getUuid());
        assertNull(emptyDoc.getUserId());
        assertNull(emptyDoc.getChallengeId());
        assertNull(emptyDoc.getCreatedAt());
    }

    @Test
    void allArgsConstructor_test() {
        BookmarkDocument doc = new BookmarkDocument(uuid, userId, challengeId, createdAt);
        Assertions.assertNotNull(doc);
        assertEquals(uuid, doc.getUuid());
        assertEquals(userId, doc.getUserId());
        assertEquals(challengeId, doc.getChallengeId());
        assertEquals(createdAt, doc.getCreatedAt());
    }

    @Test
    void settersAndGetters_test() {
        UUID newUuid = UUID.randomUUID();
        UUID newUserId = UUID.randomUUID();
        UUID newChallengeId = UUID.randomUUID();
        LocalDateTime newCreatedAt = LocalDateTime.now().plusDays(1);

        bookmarkDocument.setUuid(newUuid);
        bookmarkDocument.setUserId(newUserId);
        bookmarkDocument.setChallengeId(newChallengeId);
        bookmarkDocument.setCreatedAt(newCreatedAt);

        assertEquals(newUuid, bookmarkDocument.getUuid());
        assertEquals(newUserId, bookmarkDocument.getUserId());
        assertEquals(newChallengeId, bookmarkDocument.getChallengeId());
        assertEquals(newCreatedAt, bookmarkDocument.getCreatedAt());
    }

   
    @Test
    void builder_WithPartialFields_test() {
        BookmarkDocument doc = BookmarkDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .build();

        assertNotNull(doc);
        assertEquals(uuid, doc.getUuid());
        assertEquals(userId, doc.getUserId());
        assertNull(doc.getChallengeId());
        assertNull(doc.getCreatedAt());
    }

}
