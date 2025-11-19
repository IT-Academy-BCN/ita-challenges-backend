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
    private BookmarkDocument bookmarkDocument2;

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

        bookmarkDocument2 = BookmarkDocument.builder()
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
    void equalsAndHashCode_test() {
        BookmarkDocument doc1 = new BookmarkDocument(uuid, userId, challengeId, createdAt);
        BookmarkDocument doc2 = new BookmarkDocument(uuid, userId, challengeId, createdAt);

        assertEquals(doc1, doc2);
        assertEquals(doc1.hashCode(), doc2.hashCode());

        doc2.setChallengeId(UUID.randomUUID());
        assertNotEquals(doc1, doc2);
        assertNotEquals(doc1.hashCode(), doc2.hashCode());

        assertNotEquals(null, doc1);
        assertNotEquals("string", doc1);
    }

    @Test
    void builder_test() {
        BookmarkDocument doc = BookmarkDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        Assertions.assertNotNull(doc);
        assertEquals(uuid, doc.getUuid());
        assertEquals(userId, doc.getUserId());
        assertEquals(challengeId, doc.getChallengeId());
        assertEquals(createdAt, doc.getCreatedAt());
    }

    @Test
    void builderHandlesNullValues_test() {
        BookmarkDocument doc = BookmarkDocument.builder()
                .uuid(null)
                .userId(null)
                .challengeId(null)
                .createdAt(null)
                .build();

        Assertions.assertNotNull(doc);
        assertNull(doc.getUuid());
        assertNull(doc.getUserId());
        assertNull(doc.getChallengeId());
        assertNull(doc.getCreatedAt());
    }

    @Test
    void settersHandleNullValues_test() {
        bookmarkDocument.setUuid(null);
        bookmarkDocument.setUserId(null);
        bookmarkDocument.setChallengeId(null);
        bookmarkDocument.setCreatedAt(null);

        assertNull(bookmarkDocument.getUuid());
        assertNull(bookmarkDocument.getUserId());
        assertNull(bookmarkDocument.getChallengeId());
        assertNull(bookmarkDocument.getCreatedAt());
    }

    @Test
    void equals_Symmetric_test() {
        assertEquals(bookmarkDocument, bookmarkDocument2);
        assertEquals(bookmarkDocument2, bookmarkDocument);
    }

    @Test
    void equals_Transitive_test() {
        BookmarkDocument doc3 = new BookmarkDocument(uuid, userId, challengeId, createdAt);
        assertEquals(bookmarkDocument, bookmarkDocument2);
        assertEquals(bookmarkDocument2, doc3);
        assertEquals(bookmarkDocument, doc3);
    }

    @Test
    void equals_Consistent_test() {
        boolean firstResult = bookmarkDocument.equals(bookmarkDocument2);
        boolean secondResult = bookmarkDocument.equals(bookmarkDocument2);
        assertEquals(firstResult, secondResult);
    }

    @Test
    void hashCode_Consistent_test() {
        int firstHashCode = bookmarkDocument.hashCode();
        int secondHashCode = bookmarkDocument.hashCode();
        assertEquals(firstHashCode, secondHashCode);
    }

    @Test
    void hashCode_EqualObjectsHaveEqualHashCodes_test() {
        assertEquals(bookmarkDocument.hashCode(), bookmarkDocument2.hashCode());
    }

    @Test
    void toString_ContainsAllFields_test() {
        String str = bookmarkDocument.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("BookmarkDocument"));
        assertTrue(str.contains("uuid=" + uuid));
        assertTrue(str.contains("userId=" + userId));
        assertTrue(str.contains("challengeId=" + challengeId));
        assertTrue(str.contains("createdAt=" + createdAt));
    }

    @Test
    void equals_WithDifferentUserId_test() {
        BookmarkDocument doc1 = new BookmarkDocument(uuid, UUID.randomUUID(), challengeId, createdAt);
        BookmarkDocument doc2 = new BookmarkDocument(uuid, UUID.randomUUID(), challengeId, createdAt);

        assertNotEquals(doc1, doc2);
    }

    @Test
    void equals_WithDifferentChallengeId_test() {
        BookmarkDocument doc1 = new BookmarkDocument(uuid, userId, UUID.randomUUID(), createdAt);
        BookmarkDocument doc2 = new BookmarkDocument(uuid, userId, UUID.randomUUID(), createdAt);

        assertNotEquals(doc1, doc2);
    }

    @Test
    void equals_WithDifferentCreatedAt_test() {
        BookmarkDocument doc1 = new BookmarkDocument(uuid, userId, challengeId, LocalDateTime.now());
        BookmarkDocument doc2 = new BookmarkDocument(uuid, userId, challengeId, LocalDateTime.now().plusDays(1));

        assertNotEquals(doc1, doc2);
    }

    @Test
    void equals_WithNullUuidInBoth_test() {
        BookmarkDocument doc1 = new BookmarkDocument(null, userId, challengeId, createdAt);
        BookmarkDocument doc2 = new BookmarkDocument(null, userId, challengeId, createdAt);

        assertEquals(doc1, doc2);
    }

    void equals_WithNullUserIdInBoth_test() {
        BookmarkDocument doc1 = new BookmarkDocument(uuid, null, challengeId, createdAt);
        BookmarkDocument doc2 = new BookmarkDocument(uuid, null, challengeId, createdAt);

        assertEquals(doc1, doc2);
    }

    @Test
    void equals_WithNullChallengeIdInBoth_test() {
        BookmarkDocument doc1 = new BookmarkDocument(uuid, userId, null, createdAt);
        BookmarkDocument doc2 = new BookmarkDocument(uuid, userId, null, createdAt);

        assertEquals(doc1, doc2);
    }

    @Test
    void equals_WithNullCreatedAtInBoth_test() {
        BookmarkDocument doc1 = new BookmarkDocument(uuid, userId, challengeId, null);
        BookmarkDocument doc2 = new BookmarkDocument(uuid, userId, challengeId, null);

        assertEquals(doc1, doc2);
    }

    @Test
    void hashCode_WithNullFields_returnsConsistentHash_test() {
        BookmarkDocument doc = new BookmarkDocument();
        int hash1 = doc.hashCode();
        int hash2 = doc.hashCode();
        assertEquals(hash1, hash2);
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

    @Test
    void builder_WithOnlyRequiredFields_test() {
        BookmarkDocument doc = BookmarkDocument.builder()
                .uuid(uuid)
                .build();

        assertNotNull(doc);
        assertEquals(uuid, doc.getUuid());
        assertNull(doc.getUserId());
        assertNull(doc.getChallengeId());
        assertNull(doc.getCreatedAt());
    }

    @Test
    void builder_WithSameValues_createsEqualObjects_test() {
        BookmarkDocument doc1 = BookmarkDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        BookmarkDocument doc2 = BookmarkDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        assertEquals(doc1, doc2);
        assertEquals(doc1.hashCode(), doc2.hashCode());
    }

    @Test
    void equals_WithDifferentUuid_returnsFalse_test() {
        BookmarkDocument other = BookmarkDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        assertNotEquals(bookmarkDocument, other);
    }

    @Test
    void equals_WithDifferentUserId_returnsFalse_test() {
        BookmarkDocument other = BookmarkDocument.builder()
                .uuid(uuid)
                .userId(UUID.randomUUID())
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        assertNotEquals(bookmarkDocument, other);
    }

    @Test
    void equals_WithDifferentChallengeId_returnsFalse_test() {
        BookmarkDocument other = BookmarkDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(UUID.randomUUID())
                .createdAt(createdAt)
                .build();

        assertNotEquals(bookmarkDocument, other);
    }

    @Test
    void toString_WithNullFields_doesNotThrow_test() {
        BookmarkDocument doc = new BookmarkDocument();
        assertDoesNotThrow(doc::toString);
    }

    @Test
    void hashCode_WithDifferentObjects_returnsDifferentHashes_test() {
        BookmarkDocument doc1 = BookmarkDocument.builder()
                .uuid(UUID.randomUUID())
                .build();
        
        BookmarkDocument doc2 = BookmarkDocument.builder()
                .uuid(UUID.randomUUID())
                .build();
        
        assertNotEquals(doc1.hashCode(), doc2.hashCode());
    }
}
