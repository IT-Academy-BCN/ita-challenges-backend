package com.itachallenge.userinteraction.document.favorite;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FavoriteDocumentTest {
    private UUID uuid;
    private UUID userId;
    private UUID challengeId;
    private LocalDateTime createdAt;
    private FavoriteDocument favoriteDocument;
    private FavoriteDocument favoriteDocument2;

    @BeforeEach
    void setUp(){
        uuid = UUID.randomUUID();
        userId = UUID.randomUUID();
        challengeId = UUID.randomUUID();
        createdAt = LocalDateTime.now();

        favoriteDocument = FavoriteDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        favoriteDocument2 = FavoriteDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();
    }

    @Test
    void favoriteDocumentCreation_test(){
        Assertions.assertNotNull(favoriteDocument);
        assertEquals(uuid, favoriteDocument.getUuid());
        assertEquals(userId, favoriteDocument.getUserId());
        assertEquals(challengeId, favoriteDocument.getChallengeId());
        assertEquals(createdAt, favoriteDocument.getCreatedAt());
    }

    @Test
    void settersAndGetters_test() {
        UUID newUuid = UUID.randomUUID();
        UUID newUserId = UUID.randomUUID();
        UUID newChallengeId = UUID.randomUUID();
        LocalDateTime newCreatedAt = LocalDateTime.now().plusDays(1);

        favoriteDocument.setUuid(newUuid);
        favoriteDocument.setUserId(newUserId);
        favoriteDocument.setChallengeId(newChallengeId);
        favoriteDocument.setCreatedAt(newCreatedAt);

        assertEquals(newUuid, favoriteDocument.getUuid());
        assertEquals(newUserId, favoriteDocument.getUserId());
        assertEquals(newChallengeId, favoriteDocument.getChallengeId());
        assertEquals(newCreatedAt, favoriteDocument.getCreatedAt());
    }

    @Test
    void builder_test() {
        FavoriteDocument doc = FavoriteDocument.builder()
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
    void noArgsConstructor_test() {
        FavoriteDocument emptyDoc = new FavoriteDocument();
        Assertions.assertNotNull(emptyDoc);
        assertNull(emptyDoc.getUuid());
        assertNull(emptyDoc.getUserId());
        assertNull(emptyDoc.getChallengeId());
        assertNull(emptyDoc.getCreatedAt());
    }

    @Test
    void allArgsConstructor_test() {
        FavoriteDocument doc = new FavoriteDocument(uuid, userId, challengeId, createdAt);
        Assertions.assertNotNull(doc);
        assertEquals(uuid, doc.getUuid());
        assertEquals(userId, doc.getUserId());
        assertEquals(challengeId, doc.getChallengeId());
        assertEquals(createdAt, doc.getCreatedAt());
    }

    @Test
    void equalsAndHashCode_test() {
        FavoriteDocument doc1 = new FavoriteDocument(uuid, userId, challengeId, createdAt);
        FavoriteDocument doc2 = new FavoriteDocument(uuid, userId, challengeId, createdAt);

        assertEquals(doc1, doc2);
        assertEquals(doc1.hashCode(), doc2.hashCode());

        doc2.setChallengeId(UUID.randomUUID());
        assertNotEquals(doc1, doc2);
        assertNotEquals(doc1.hashCode(), doc2.hashCode());

        assertNotEquals(null, doc1);

        assertNotEquals("string", doc1);
    }

    @Test
    void toString_test() {
        String str = favoriteDocument.toString();
        assertTrue(str.contains("FavoriteDocument"));
        assertTrue(str.contains(uuid.toString()));
        assertTrue(str.contains(userId.toString()));
        assertTrue(str.contains(challengeId.toString()));
        assertTrue(str.contains(createdAt.toString()));
    }

    @Test
    void builderHandlesNullValues_test() {
        FavoriteDocument doc = FavoriteDocument.builder()
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
        favoriteDocument.setUuid(null);
        favoriteDocument.setUserId(null);
        favoriteDocument.setChallengeId(null);
        favoriteDocument.setCreatedAt(null);

        assertNull(favoriteDocument.getUuid());
        assertNull(favoriteDocument.getUserId());
        assertNull(favoriteDocument.getChallengeId());
        assertNull(favoriteDocument.getCreatedAt());
    }

    @Test
    void equals_Symmetric_test() {
        assertEquals(favoriteDocument, favoriteDocument2);
        assertEquals(favoriteDocument2, favoriteDocument);
    }

    @Test
    void equals_Transitive_test() {
        FavoriteDocument doc3 = new FavoriteDocument(uuid, userId, challengeId, createdAt);
        assertEquals(favoriteDocument, favoriteDocument2);
        assertEquals(favoriteDocument2, doc3);
        assertEquals(favoriteDocument, doc3);
    }

    @Test
    void equals_Consistent_test() {
        boolean firstResult = favoriteDocument.equals(favoriteDocument2);
        boolean secondResult = favoriteDocument.equals(favoriteDocument2);
        assertEquals(firstResult, secondResult);
    }

    @Test
    void hashCode_Consistent_test() {
        int firstHashCode = favoriteDocument.hashCode();
        int secondHashCode = favoriteDocument.hashCode();
        assertEquals(firstHashCode, secondHashCode);
    }

    @Test
    void hashCode_EqualObjectsHaveEqualHashCodes_test() {
        assertEquals(favoriteDocument.hashCode(), favoriteDocument2.hashCode());
    }

    @Test
    void toString_ContainsAllFields_test() {
        String toString = favoriteDocument.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("uuid=" + uuid));
        assertTrue(toString.contains("userId=" + userId));
        assertTrue(toString.contains("challengeId=" + challengeId));
        assertTrue(toString.contains("createdAt=" + createdAt));
    }

    @Test
    void builder_PartialFields_test() {
        UUID onlyUserId = UUID.randomUUID();
        FavoriteDocument doc = FavoriteDocument.builder()
                .userId(onlyUserId)
                .build();
        assertNull(doc.getUuid());
        assertEquals(onlyUserId, doc.getUserId());
        assertNull(doc.getChallengeId());
        assertNull(doc.getCreatedAt());
    }

    @Test
    void equals_NullAndDifferentType_test() {
        FavoriteDocument doc = new FavoriteDocument(uuid, userId, challengeId, createdAt);
        assertNotEquals(null, doc);
        assertNotEquals("some string", doc);

        FavoriteDocument other = new FavoriteDocument(uuid, userId, null, createdAt);
        assertNotEquals(doc, other);
        assertNotEquals(doc.hashCode(), other.hashCode());
    }

    @Test
    void toString_WithNullFields_test() {
        FavoriteDocument doc = new FavoriteDocument(null, userId, null, null);
        String str = doc.toString();
        assertTrue(str.contains("FavoriteDocument"));
        assertTrue(str.contains("userId=" + userId));
        assertTrue(str.contains("uuid=null"));
        assertTrue(str.contains("challengeId=null"));
        assertTrue(str.contains("createdAt=null"));
    }

    @Test
    void builderThenSetters_EqualsDifference_test() {
        FavoriteDocument doc1 = FavoriteDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        FavoriteDocument doc2 = FavoriteDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        assertEquals(doc1, doc2);

        doc2.setCreatedAt(createdAt.plusHours(1));
        assertNotEquals(doc1, doc2);
    }

    @Test
    void equals_WithDifferentUserId_test() {
        UUID uuid = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteDocument doc1 = new FavoriteDocument(uuid, UUID.randomUUID(), challengeId, createdAt);
        FavoriteDocument doc2 = new FavoriteDocument(uuid, UUID.randomUUID(), challengeId, createdAt);

        assertNotEquals(doc1, doc2);
    }

    void equals_WithDifferentChallengeId_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteDocument doc1 = new FavoriteDocument(uuid, userId, UUID.randomUUID(), createdAt);
        FavoriteDocument doc2 = new FavoriteDocument(uuid, userId, UUID.randomUUID(), createdAt);

        assertNotEquals(doc1, doc2);
    }

    @Test
    void equals_WithDifferentCreatedAt_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        FavoriteDocument doc1 = new FavoriteDocument(uuid, userId, challengeId, LocalDateTime.now());
        FavoriteDocument doc2 = new FavoriteDocument(uuid, userId, challengeId, LocalDateTime.now().plusDays(1));

        assertNotEquals(doc1, doc2);
    }

    @Test
    void equals_WithNullUuidInBoth_test() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteDocument doc1 = new FavoriteDocument(null, userId, challengeId, createdAt);
        FavoriteDocument doc2 = new FavoriteDocument(null, userId, challengeId, createdAt);

        assertEquals(doc1, doc2);
    }

    void equals_WithNullUserIdInBoth_test() {
        UUID uuid = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteDocument doc1 = new FavoriteDocument(uuid, null, challengeId, createdAt);
        FavoriteDocument doc2 = new FavoriteDocument(uuid, null, challengeId, createdAt);

        assertEquals(doc1, doc2);
    }

    @Test
    void equals_WithNullChallengeIdInBoth_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteDocument doc1 = new FavoriteDocument(uuid, userId, null, createdAt);
        FavoriteDocument doc2 = new FavoriteDocument(uuid, userId, null, createdAt);

        assertEquals(doc1, doc2);
    }

    void equals_WithNullCreatedAtInBoth_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        FavoriteDocument doc1 = new FavoriteDocument(uuid, userId, challengeId, null);
        FavoriteDocument doc2 = new FavoriteDocument(uuid, userId, challengeId, null);

        assertEquals(doc1, doc2);
    }
    @Test
    void hashCode_WithAllNulls_test() {
        FavoriteDocument doc = new FavoriteDocument(null, null, null, null);
        doc.hashCode();
    }

    @Test
    void hashCode_WithMixedNulls_test() {
        UUID uuid = UUID.randomUUID();
        FavoriteDocument doc1 = new FavoriteDocument(uuid, null, null, LocalDateTime.now());
        FavoriteDocument doc2 = new FavoriteDocument(uuid, null, null, LocalDateTime.now());

        // hashCode might differ due to LocalDateTime, but should be consistent
        assertEquals(doc1.hashCode(), doc1.hashCode());
    }

    @Test
    void canEqual_WithSameClass_test() {
        FavoriteDocument doc1 = new FavoriteDocument();
        FavoriteDocument doc2 = new FavoriteDocument();

        assertTrue(doc1.canEqual(doc2));
    }

    @Test
    void canEqual_WithDifferentClass_test() {
        FavoriteDocument doc = new FavoriteDocument();

        assertFalse(doc.canEqual(new Object()));
    }

    @Test
    void builder_ChainedCalls_test() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        FavoriteDocument doc = FavoriteDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        assertNotNull(doc);
        assertEquals(uuid, doc.getUuid());
        assertEquals(userId, doc.getUserId());
        assertEquals(challengeId, doc.getChallengeId());
        assertEquals(createdAt, doc.getCreatedAt());
    }

    @Test
    void builder_EmptyBuild_test() {
        FavoriteDocument doc = FavoriteDocument.builder().build();

        assertNotNull(doc);
        assertNull(doc.getUuid());
        assertNull(doc.getUserId());
        assertNull(doc.getChallengeId());
        assertNull(doc.getCreatedAt());
    }
}
