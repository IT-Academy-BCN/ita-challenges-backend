package com.itachallenge.challenge.documents;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DetailTest {

    @Test
    public void testDetailClass() {
        // Init data
        String description = "Challenge description";
        List<Example> examples = Arrays.asList(
                new Example(UUID.fromString("0172b4b5-6a4f-4239-b5da-1638e7fb81f3"), "Input 1"),
                new Example(UUID.fromString("46177adc-8d9f-4975-9661-d8b80ebd29f8"), "Input 2")
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
