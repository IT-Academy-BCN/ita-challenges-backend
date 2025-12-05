package com.itachallenge.userinteraction.document.bookmark;

import java.time.LocalDateTime;
import java.util.UUID;

import com.itachallenge.userinteraction.document.favorite.FavoriteDocument;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookmarkDocumentTest {

    @Test
    void constructor_shouldSetBaseInteractionFields() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        BookmarkDocument document = BookmarkDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        assertEquals(uuid, document.getUuid());
        assertEquals(userId, document.getUserId());
        assertEquals(challengeId, document.getChallengeId());
        assertEquals(createdAt, document.getCreatedAt());
    }

    @Test
    void equalsHashCodeAndToString_shouldWorkWithSameIdentity() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        BookmarkDocument a = BookmarkDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        BookmarkDocument b = BookmarkDocument.builder()
                .uuid(uuid)
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        BookmarkDocument different = BookmarkDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        assertNotEquals(a, different);
        assertNotEquals(a, null);
        assertNotEquals(a, "some string");

        assertNotNull(a.toString());
    }

}
