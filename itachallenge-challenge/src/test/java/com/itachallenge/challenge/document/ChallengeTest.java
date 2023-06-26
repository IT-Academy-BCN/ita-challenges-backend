package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChallengeTest {

    @Test
    void getUuid() {
        UUID uuid = UUID.randomUUID();
        Challenge challenge = new Challenge(uuid, null, null, null, null, null, null, null, null);
        assertEquals(uuid, challenge.getUuid());
    }

    @Test
    void getLevel() {
        String level = "Intermediate";
        Challenge challenge = new Challenge(null, level, null, null, null, null, null, null, null);
        assertEquals(level, challenge.getLevel());
    }

    @Test
    void getTitle() {
        String title = "Test Challenge";
        Challenge challenge = new Challenge(null, null, title, null, null, null, null, null, null);
        assertEquals(title, challenge.getTitle());
    }

    @Test
    void getLanguages() {
        UUID uuid = UUID.randomUUID();
        Set<UUID> uuidSet = Set.of(uuid);
        Set<Language> languages = Set.of(new Language(1, "Java", uuidSet), new Language(2, "Python", uuidSet));

        Challenge challenge = new Challenge(null, null, null, languages, null, null, null, null, null);
        assertEquals(languages, challenge.getLanguages());
    }

    @Test
    void getCreationDate() {
        LocalDateTime creationDate = now();
        Challenge challenge = new Challenge(null, null, null, null, creationDate, null, null, null, null);
        assertTrue(creationDate.truncatedTo(ChronoUnit.SECONDS).isEqual(challenge.getCreationDate().truncatedTo(ChronoUnit.SECONDS)));


    }

    @Test
    void getDetail() {
        Detail detail = new Detail(null, null, null);
        Challenge challenge = new Challenge(null, null, null, null, null, detail, null, null, null);
        assertEquals(detail, challenge.getDetail());
    }

    @Test
    void getSolutions() {
        List<Solution> solutions = new ArrayList<>();
        Solution solution1 = new Solution(UUID.randomUUID(), "Solution 1", 1);
        Solution solution2 = new Solution(UUID.randomUUID(), "Solution 2", 2);
        solutions.add(solution1);
        solutions.add(solution2);
        Challenge challenge = new Challenge(null, null, null, null, null, null, solutions, null, null);
        assertEquals(solutions, challenge.getSolutions());
    }

    @Test
    void getRelatedChallenges() {
        Set<UUID> relatedChallenges = new HashSet<>();
        UUID challengeId1 = UUID.randomUUID();
        UUID challengeId2 = UUID.randomUUID();
        relatedChallenges.add(challengeId1);
        relatedChallenges.add(challengeId2);
        Challenge challenge = new Challenge(null, null, null, null, null, null, null, relatedChallenges, null);
        assertEquals(relatedChallenges, challenge.getRelatedChallenges());
    }

    @Test
    void getResources() {
        Set<UUID> resources = new HashSet<>();
        UUID resourceId1 = UUID.randomUUID();
        UUID resourceId2 = UUID.randomUUID();
        resources.add(resourceId1);
        resources.add(resourceId2);
        Challenge challenge = new Challenge(null, null, null, null, null, null, null, null, resources);
        assertEquals(resources, challenge.getResources());
    }
}
