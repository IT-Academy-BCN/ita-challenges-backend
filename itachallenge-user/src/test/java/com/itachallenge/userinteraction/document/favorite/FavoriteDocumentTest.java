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
    }

    @Test
    void favoriteDocumentCreation_test(){
        Assertions.assertNotNull(favoriteDocument);
        assertEquals(uuid, favoriteDocument.getUuid());
        assertEquals(userId, favoriteDocument.getUserId());
        assertEquals(challengeId, favoriteDocument.getChallengeId());
        assertEquals(createdAt, favoriteDocument.getCreatedAt());
    }

//    @Test
//    void settersAndGetters_test() {
//        UUID newUuid = UUID.randomUUID();
//        UUID newUserId = UUID.randomUUID();
//        UUID newChallengeId = UUID.randomUUID();
//        LocalDateTime newCreatedAt = LocalDateTime.now().plusDays(1);
//
//        favoriteDocument.setUuid(newUuid);
//        favoriteDocument.setUserId(newUserId);
//        favoriteDocument.setChallengeId(newChallengeId);
//        favoriteDocument.setCreatedAt(newCreatedAt);
//
//        assertEquals(newUuid, favoriteDocument.getUuid());
//        assertEquals(newUserId, favoriteDocument.getUserId());
//        assertEquals(newChallengeId, favoriteDocument.getChallengeId());
//        assertEquals(newCreatedAt, favoriteDocument.getCreatedAt());
//    }

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
    void toString_test() {
        String str = favoriteDocument.toString();
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
    void toString_ContainsAllFields_test() {
        String toString = favoriteDocument.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("uuid=" + uuid));
        assertTrue(toString.contains("userId=" + userId));
        assertTrue(toString.contains("challengeId=" + challengeId));
        assertTrue(toString.contains("createdAt=" + createdAt));
    }

    @Test
    void equalsAndHashCode_singleTestCoverage() {
        // Two objects with identical values
        FavoriteDocument a = FavoriteDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        FavoriteDocument b = FavoriteDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        // Another object with a different field
        FavoriteDocument c = FavoriteDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        // equals
        assertEquals(a, b, "Objects with identical values should be equal");
        assertNotEquals(a, c, "Objects with different values should not be equal");
        assertNotEquals(null, a, "Object must not be equal to null");
        assertNotEquals("string", a, "Object must not be equal to object of another type");

        // hashCode
        assertEquals(a.hashCode(), b.hashCode(), "Equal objects must have the same hashCode");
        // Consistency check
        int first = a.hashCode();
        int second = a.hashCode();
        assertEquals(first, second, "hashCode should be consistent across invocations");
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


}
