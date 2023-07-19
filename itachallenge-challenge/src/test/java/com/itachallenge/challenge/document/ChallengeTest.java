package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChallengeTest {

    @Test
    void getUuid() {
        UUID uuid = UUID.randomUUID();
        ChallengeDocument challenge = new ChallengeDocument(uuid, null, null, null, null, null, null, null, null);
        assertEquals(uuid, challenge.getUuid());
    }

    @Test
    void getTitle() {
        String title = "Test Challenge";
        ChallengeDocument challenge = new ChallengeDocument(null, title, null, null, null, null, null, null, null);
        assertEquals(title, challenge.getTitle());
    }

    @Test
    void getLevel() {
        String level = "Intermediate";
        ChallengeDocument challenge = new ChallengeDocument(null, null, level, null, null, null, null, null, null);
        assertEquals(level, challenge.getLevel());
    }

    @Test
    void getCreationDate() {
        LocalDateTime creationDate = now();
        ChallengeDocument challenge = new ChallengeDocument(null, null, null, creationDate, null, null, null, null, null);
        assertTrue(creationDate.truncatedTo(ChronoUnit.SECONDS).isEqual(challenge.getCreationDate().truncatedTo(ChronoUnit.SECONDS)));
    }

    @Test
    void getDetail() {
        DetailDocument detail = new DetailDocument(null, null, null);
        ChallengeDocument challenge = new ChallengeDocument(null, null, null, null, detail, null, null, null, null);
        assertEquals(detail, challenge.getDetail());
    }

    @Test
    void getLanguages() {
        UUID uuid = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        UUID uuid2 = UUID.fromString("409c9fe8-74de-4db3-81a1-a55280cf92ef");
        Set<LanguageDocument> languages = Set.of(new LanguageDocument(uuid, "Javascript"), new LanguageDocument(uuid2, "Python"));

        ChallengeDocument challenge = new ChallengeDocument(null, null, null, null, null, languages, null, null, null);
        assertEquals(languages, challenge.getLanguages());
    }

    @Test
    void getSolutions() {
        List<UUID> solutions = List.of(UUID.randomUUID(),UUID.randomUUID());

        ChallengeDocument challenge = new ChallengeDocument(null, null, null, null, null, null, solutions, null, null);
        assertEquals(solutions, challenge.getSolutions());
    }

    @Test
    void getResources() {
        Set<UUID> resources = new HashSet<>();
        UUID resourceId1 = UUID.randomUUID();
        UUID resourceId2 = UUID.randomUUID();
        resources.add(resourceId1);
        resources.add(resourceId2);
        ChallengeDocument challenge = new ChallengeDocument(null, null, null, null, null, null, null, resources, null);
        assertEquals(resources, challenge.getResources());
    }

    @Test
    void getRelatedChallenges() {
        Set<UUID> relatedChallenges = new HashSet<>();
        UUID challengeId1 = UUID.randomUUID();
        UUID challengeId2 = UUID.randomUUID();
        relatedChallenges.add(challengeId1);
        relatedChallenges.add(challengeId2);
        ChallengeDocument challenge = new ChallengeDocument(null, null, null, null, null, null, null, null, relatedChallenges);
        assertEquals(relatedChallenges, challenge.getRelatedChallenges());
    }

}