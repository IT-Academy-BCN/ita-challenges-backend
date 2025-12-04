package com.itachallenge.user.dto.userinteraction;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.itachallenge.userinteraction.document.InteractionDocument;
import org.junit.jupiter.api.Test;

class InteractionDocumentTest {

    @Test
    void equalsHashCodeAndToString_shouldCoverMainBranches() {
        UUID uuid = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now();

        InteractionDocument a = new InteractionDocument(uuid, userId, challengeId, createdAt) {};
        InteractionDocument sameValues = new InteractionDocument(uuid, userId, challengeId, createdAt) {};
        InteractionDocument differentUuid = new InteractionDocument(UUID.randomUUID(), userId, challengeId, createdAt) {};

        InteractionDocument empty1 = new InteractionDocument() {};
        InteractionDocument empty2 = new InteractionDocument() {};

        assertEquals(a, a);

        assertEquals(a, sameValues);
        assertEquals(a.hashCode(), sameValues.hashCode());

        assertNotEquals(a, differentUuid);

        assertNotEquals(a, null);

        assertNotEquals(a, "some string");

        assertEquals(empty1, empty2);
        assertEquals(empty1.hashCode(), empty2.hashCode());

        assertNotNull(a.toString());
        assertNotNull(empty1.toString());
    }
}
