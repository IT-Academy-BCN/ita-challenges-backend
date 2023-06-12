package com.itachallenge.challenge.documents;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChallengeTest {

    @Test
    public void testChallengeClass() {
        // Init data
        Example example = Example.builder()
                .idExample("d07008df-39ef-4c96-83aa-0ce0e25ed4c4")
                .exampleText("Example Text Test")
                .build();
        Detail detail = Detail.builder()
                .examples(List.of(example))
                .description("Test Description")
                .note("Test note")
                .build();
        String uuid = "123456";
        String level = "Intermediate";
        String title = "Java Challenge";
        Set<String> languages = new HashSet<>(Arrays.asList("Java", "Python"));
        LocalDate creationDate = LocalDate.now();

        List<String> solutions = Arrays.asList("Solution 1", "Solution 2");
        Set<String> related = new HashSet<>(Arrays.asList("Related 1", "Related 2"));
        Set<String> resources = new HashSet<>(Arrays.asList("Resource 1", "Resource 2"));
        Set<String> tags = new HashSet<>(Arrays.asList("Tag 1", "Tag 2"));

        // Act
        Challenge challenge = Challenge.builder()
                .uuid(uuid)
                .level(level)
                .title(title)
                .languages(languages)
                .creationDate(creationDate)
                .detail(detail)
                .solutions(solutions)
                .related(related)
                .resources(resources)
                .tags(tags)
                .build();

        // Assert
        Assertions.assertEquals(uuid, challenge.getUuid());
        Assertions.assertEquals(level, challenge.getLevel());
        Assertions.assertEquals(title, challenge.getTitle());
        Assertions.assertEquals(languages, challenge.getLanguages());
        Assertions.assertEquals(creationDate, challenge.getCreationDate());
        Assertions.assertEquals(detail, challenge.getDetail());
        Assertions.assertEquals(solutions, challenge.getSolutions());
        Assertions.assertEquals(related, challenge.getRelated());
        Assertions.assertEquals(resources, challenge.getResources());
        Assertions.assertEquals(tags, challenge.getTags());
    }

}
