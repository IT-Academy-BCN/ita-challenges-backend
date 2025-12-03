package com.itachallenge.userinteraction.document.favorite;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FavoriteDocumentTest {
    @Test
    void equalsHashCode_fullBranchCoverage() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

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

        FavoriteDocument c = FavoriteDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId)
                .createdAt(createdAt)
                .build();

        assertEquals(a, b);
        assertNotEquals(a, c);

        // equals — (Lombok "this == o")
        assertEquals(a, a);

        // equals — null (Lombok "o == null")
        assertNotEquals(null, a);

        // equals — different type (Lombok "getClass() != o.getClass()")
        assertNotEquals("some string", a);

        assertEquals(a.hashCode(), b.hashCode());

        FavoriteDocument empty = new FavoriteDocument();
        assertDoesNotThrow(empty::hashCode);

        assertNotNull(a.toString());

        FavoriteDocument n1 = new FavoriteDocument();
        FavoriteDocument n2 = new FavoriteDocument();

        assertEquals(n1, n2);
        assertEquals(n1.hashCode(), n2.hashCode());
    }

}
