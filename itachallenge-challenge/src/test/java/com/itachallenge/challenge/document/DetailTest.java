package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(description, detail.getDescription());
        assertEquals(examples, detail.getExamples());
        assertEquals(note, detail.getNote());
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
        assertEquals(description, result);
    }

    @Test
    void testSetDescription() {
        // Arrange
        String description = "Test Description";
        Detail detail = new Detail();

        // Act
        detail.setDescription(description);

        // Assert
        assertEquals(description, detail.getDescription());
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
        assertEquals(examples, result);
    }

    @Test
    void testSetExamples() {
        // Arrange
        List<Example> examples = Arrays.asList(new Example(), new Example());
        Detail detail = new Detail();

        // Act
        detail.setExamples(examples);

        // Assert
        assertEquals(examples, detail.getExamples());
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
        assertEquals(note, result);
    }

    @Test
    void testSetNote() {
        // Arrange
        String note = "Test Note";
        Detail detail = new Detail();

        // Act
        detail.setNote(note);

        // Assert
        assertEquals(note, detail.getNote());
    }

    UUID uuid_1 = UUID.fromString("8ecbfe54-fec8-11ed-be56-0242ac120002");
    UUID uuid_2 = UUID.fromString("26977eee-89f8-11ec-a8a3-0242ac120003");

    @Test
    void getDescription() {
        String description = "Test Description";
        Detail detail = new Detail(description, null, null);
        assertEquals(description, detail.getDescription());
    }

    @Test
    void getExamples() {
        List<Example> examples = new ArrayList<>();
        examples.add(new Example(uuid_1,"Example 1"));
        examples.add(new Example(uuid_2,"Example 2"));
        Detail detail = new Detail(null, examples, null);
        assertEquals(examples, detail.getExamples());
    }

    @Test
    void getNote() {
        String note = "Test Note";
        Detail detail = new Detail(null, null, note);
        assertEquals(note, detail.getNote());
    }
}
