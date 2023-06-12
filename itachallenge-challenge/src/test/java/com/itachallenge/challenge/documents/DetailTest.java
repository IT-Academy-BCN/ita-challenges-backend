package com.itachallenge.challenge.documents;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class DetailTest {

    @Test
    public void testDetailClass() {
        // Init data
        String description = "Challenge description";
        List<Example> examples = Arrays.asList(
                new Example("Example 1", "Input 1"),
                new Example("Example 2", "Input 2")
        );
        String note = "Additional note";

        // Act
        Detail detail = Detail.builder()
                .description(description)
                .examples(examples)
                .note(note)
                .build();

        // Assert
        Assertions.assertEquals(description, detail.getDescription());
        Assertions.assertEquals(examples, detail.getExamples());
        Assertions.assertEquals(note, detail.getNote());
    }

}
