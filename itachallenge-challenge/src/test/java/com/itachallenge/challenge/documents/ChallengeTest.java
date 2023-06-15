package com.itachallenge.challenge.documents;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

public class ChallengeTest {

    @Test
    public void testChallengeClass() {
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
        Set<String> languages = Set.of("4");
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
        Assertions.assertEquals(uuid, challenge.getUuid());
        Assertions.assertEquals(level, challenge.getLevel());
        Assertions.assertEquals(title, challenge.getTitle());
        Assertions.assertEquals(languages, challenge.getLanguages());
        Assertions.assertEquals(creationDate, challenge.getCreationDate());
        Assertions.assertEquals(detail, challenge.getDetail());
        Assertions.assertEquals(solutions, challenge.getSolutions());
        Assertions.assertEquals(resources, challenge.getResources());
    }

    @Test
    public void testGettersAndSetters() {
        UUID uuid = UUID.randomUUID();
        String level = "Intermediate";
        String title = "Coding Challenge";
        Set<String> languages = new HashSet<>(Arrays.asList("Java", "Python"));
        LocalDate creationDate = LocalDate.now();

        Challenge challenge = new Challenge();
        challenge.setUuid(uuid);
        challenge.setLevel(level);
        challenge.setTitle(title);
        challenge.setLanguages(languages);
        challenge.setCreationDate(creationDate);

        Assertions.assertEquals(uuid, challenge.getUuid());
        Assertions.assertEquals(level, challenge.getLevel());
        Assertions.assertEquals(title, challenge.getTitle());
        Assertions.assertEquals(languages, challenge.getLanguages());
        Assertions.assertEquals(creationDate, challenge.getCreationDate());
    }

    @Test
    public void testEqualsAndHashCode() {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        Challenge challenge1 = new Challenge();
        challenge1.setUuid(uuid1);

        Challenge challenge2 = new Challenge();
        challenge2.setUuid(uuid1);

        Challenge challenge3 = new Challenge();
        challenge3.setUuid(uuid2);

        Assertions.assertEquals(challenge1, challenge2);
        Assertions.assertNotEquals(challenge1, challenge3);
        Assertions.assertEquals(challenge1.hashCode(), challenge2.hashCode());
        Assertions.assertNotEquals(challenge1.hashCode(), challenge3.hashCode());
    }

    @Test
    public void testToString() {
        UUID uuid = UUID.randomUUID();
        String level = "Intermediate";
        String title = "Coding Challenge";
        Set<String> languages = Set.of("Java", "Python");
        LocalDate creationDate = LocalDate.now();

        Challenge challenge = new Challenge();
        challenge.setUuid(uuid);
        challenge.setLevel(level);
        challenge.setTitle(title);
        challenge.setLanguages(languages);
        challenge.setCreationDate(creationDate);

        String expectedToString = "Challenge(uuid=" + uuid + ", level=" + level + ", title=" + title +
                ", languages=" + languages + ", creationDate=" + creationDate + ", detail=null, solutions=null, relatedChallenges=null, resources=null"+")";
        Assertions.assertEquals(expectedToString, challenge.toString());
    }

}
