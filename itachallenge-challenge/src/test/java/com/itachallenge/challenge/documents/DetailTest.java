package com.itachallenge.challenge.documents;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

class DetailTest {

    @Test
    void testDetailClass() {
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

    @Test
    void testGetDescription() {
        // Arrange
        String description = "Test Description";
        Detail detail = new Detail();
        detail.setDescription(description);

        // Act
        String result = detail.getDescription();

        // Assert
        Assertions.assertEquals(description, result);
    }

    @Test
    void testSetDescription() {
        // Arrange
        String description = "Test Description";
        Detail detail = new Detail();

        // Act
        detail.setDescription(description);

        // Assert
        Assertions.assertEquals(description, detail.getDescription());
    }

    @Test
    void testGetExamples() {
        // Arrange
        List<Example> examples = Arrays.asList(new Example(), new Example());
        Detail detail = new Detail();
        detail.setExamples(examples);

        // Act
        List<Example> result = detail.getExamples();

        // Assert
        Assertions.assertEquals(examples, result);
    }

    @Test
    void testSetExamples() {
        // Arrange
        List<Example> examples = Arrays.asList(new Example(), new Example());
        Detail detail = new Detail();

        // Act
        detail.setExamples(examples);

        // Assert
        Assertions.assertEquals(examples, detail.getExamples());
    }

    @Test
    void testGetNote() {
        // Arrange
        String note = "Test Note";
        Detail detail = new Detail();
        detail.setNote(note);

        // Act
        String result = detail.getNote();

        // Assert
        Assertions.assertEquals(note, result);
    }

    @Test
    void testSetNote() {
        // Arrange
        String note = "Test Note";
        Detail detail = new Detail();

        // Act
        detail.setNote(note);

        // Assert
        Assertions.assertEquals(note, detail.getNote());
    }



}
