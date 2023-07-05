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
        UUID idChallenge = UUID.randomUUID();
        ChallengeDocument challenge = new ChallengeDocument(idChallenge, null, null, null, null, null, null, null, null);

        assertEquals(idChallenge, challenge.getIdChallenge());
    }

    @Test
    void getLevel() {
        String level = "Intermediate";
        ChallengeDocument challenge = new ChallengeDocument(null, level, null, null, null, null, null, null, null);

        assertEquals(level, challenge.getLevel());
    }

    @Test
    void getTitle() {
        String title = "Test Challenge";
        ChallengeDocument challenge = new ChallengeDocument(null, null, title, null, null, null, null, null, null);

        assertEquals(title, challenge.getTitle());
    }

    @Test
    void getLanguages() {
        UUID idLanguage = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");
        UUID idLanguage2 = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80297");
        UUID idChallenge = UUID.randomUUID();
        Set<UUID> idChallenges = Set.of(idChallenge);
        Set<LanguageDocument> languages = Set.of(new LanguageDocument(idLanguage, "Java", idChallenges), new LanguageDocument(idLanguage2, "Python", idChallenges));
        ChallengeDocument challenge = new ChallengeDocument(null, null, null, languages, null, null, null, null, null);

        assertEquals(languages, challenge.getLanguages());
    }

    @Test
    void getCreationDate() {
        LocalDateTime creationDate = now();
        ChallengeDocument challenge = new ChallengeDocument(null, null, null, null, creationDate, null, null, null, null);

        assertTrue(creationDate.truncatedTo(ChronoUnit.SECONDS).isEqual(challenge.getCreationDate().truncatedTo(ChronoUnit.SECONDS)));
    }

    @Test
    void getDetail() {
        DetailDocument detail = new DetailDocument(null, null, null);
        ChallengeDocument challenge = new ChallengeDocument(null, null, null, null, null, detail, null, null, null);

        assertEquals(detail, challenge.getDetail());
    }

    @Test
    void getSolutions() {
        UUID idLanguage = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296");
        UUID idLanguage2 = UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80297");
        List<SolutionDocument> solutions = new ArrayList<>();
        SolutionDocument solution1 = new SolutionDocument(UUID.randomUUID(), "Solution 1", idLanguage);
        SolutionDocument solution2 = new SolutionDocument(UUID.randomUUID(), "Solution 2", idLanguage2);
        solutions.add(solution1);
        solutions.add(solution2);
        ChallengeDocument challenge = new ChallengeDocument(null, null, null, null, null, null, solutions, null, null);

        assertEquals(solutions, challenge.getSolutions());
    }

    @Test
    void getRelatedChallenges() {
        Set<UUID> relatedChallenges = new HashSet<>();
        UUID idChallenge = UUID.randomUUID();
        UUID idChallenge2 = UUID.randomUUID();
        relatedChallenges.add(idChallenge);
        relatedChallenges.add(idChallenge2);
        ChallengeDocument challenge = new ChallengeDocument(null, null, null, null, null, null, null, relatedChallenges, null);

        assertEquals(relatedChallenges, challenge.getRelatedChallenges());
    }

    @Test
    void getResources() {
        Set<UUID> resources = new HashSet<>();
        UUID idResource = UUID.randomUUID();
        UUID idResource2 = UUID.randomUUID();
        resources.add(idResource);
        resources.add(idResource2);
        ChallengeDocument challenge = new ChallengeDocument(null, null, null, null, null, null, null, null, resources);

        assertEquals(resources, challenge.getResources());
    }
}
