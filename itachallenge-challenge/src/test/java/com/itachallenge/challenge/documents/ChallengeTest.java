package com.itachallenge.challenge.documents;


import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ChallengeTest {

    @Test
    void testChallengeClass() {
        // Init data
        Example example = Example.builder()
                .idExample(UUID.fromString("d07008df-39ef-4c96-83aa-0ce0e25ed4c4"))
                .exampleText("Example Text Test")
                .build();
        Detail detail = Detail.builder()
                .examples(List.of(example))
                .description("Test Description")
                .note("Test note")
                .build();
        UUID uuid = UUID.randomUUID();
        String level = "Intermediate";
        String title = "Java Challenge";
        Set<UUID> languages = Set.of(UUID.randomUUID());
        LocalDate creationDate = LocalDate.now();

        List<UUID> solutions = List.of(UUID.randomUUID());
        Set<UUID> resources = Set.of(UUID.randomUUID());


        // Act
        Challenge challenge = Challenge.builder()
                .uuid(uuid)
                .level(level)
                .title(title)
                .languages(languages)
                .creationDate(creationDate)
                .detail(detail)
                .solutions(solutions)
                .resources(resources)
                .build();

        // Assert
        assertEquals(uuid, challenge.getUuid());
        assertEquals(level, challenge.getLevel());
        assertEquals(title, challenge.getTitle());
        assertEquals(languages, challenge.getLanguages());
        assertEquals(creationDate, challenge.getCreationDate());
        assertEquals(detail, challenge.getDetail());
        assertEquals(solutions, challenge.getSolutions());
        assertEquals(resources, challenge.getResources());
    }

    @Test
    void testGettersAndSetters() {
        UUID uuid = UUID.randomUUID();
        String level = "Intermediate";
        String title = "Coding Challenge";
        Set<UUID> languages = Set.of(UUID.randomUUID());
        LocalDate creationDate = LocalDate.now();

        Challenge challenge = new Challenge();
        challenge.setUuid(uuid);
        challenge.setLevel(level);
        challenge.setTitle(title);
        challenge.setLanguages(languages);
        challenge.setCreationDate(creationDate);

        assertEquals(uuid, challenge.getUuid());
        assertEquals(level, challenge.getLevel());
        assertEquals(title, challenge.getTitle());
        assertEquals(languages, challenge.getLanguages());
        assertEquals(creationDate, challenge.getCreationDate());
    }

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
        Set<UUID> languages = Set.of(UUID.randomUUID());

        Challenge challenge = new Challenge(null, null, null, languages, null, null, null, null, null);
        assertEquals(languages, challenge.getLanguages());
    }

    @Test
    void getDetail() {
        Detail detail = new Detail(null, null, null);
        Challenge challenge = new Challenge(null, null, null, null, null, detail, null, null, null);
        assertEquals(detail, challenge.getDetail());
    }

    @Test
    void getSolutions() {
        List<UUID> solutions = List.of(UUID.randomUUID());
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
