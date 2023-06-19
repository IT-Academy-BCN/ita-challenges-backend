package com.itachallenge.challenge.documents;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExampleTest {

    @Test
    void testExampleClass() {
        // Arrange
        UUID idExample = UUID.fromString("d4656966-5277-48ce-9d13-4b41ef901722");
        String exampleText = "Example text";

        // Act
        Example example = Example.builder()
                .idExample(idExample)
                .exampleText(exampleText)
                .build();

        // Assert
        assertEquals(idExample, example.getIdExample());
        assertEquals(exampleText, example.getExampleText());
    }

    @Test
    void testGetIdExample() {
        // Arrange
        UUID idExample = UUID.randomUUID();
        Example example = new Example();
        example.setIdExample(idExample);

        // Act
        UUID result = example.getIdExample();

        // Assert
        assertEquals(idExample, result);
    }

    @Test
    void testSetIdExample() {
        // Arrange
        UUID idExample = UUID.randomUUID();
        Example example = new Example();

        // Act
        example.setIdExample(idExample);

        // Assert
        assertEquals(idExample, example.getIdExample());
    }

    @Test
    void testGetExampleText() {
        // Arrange
        String exampleText = "Test Example Text";
        Example example = new Example();
        example.setExampleText(exampleText);

        // Act
        String result = example.getExampleText();

        // Assert
        assertEquals(exampleText, result);
    }

    @Test
    void testSetExampleText() {
        // Arrange
        String exampleText = "Test Example Text";
        Example example = new Example();

        // Act
        example.setExampleText(exampleText);

        // Assert
        assertEquals(exampleText, example.getExampleText());
    }

    @Test
    void getIdExample() {
    UUID idExample = UUID.randomUUID();
    Example example = new Example(idExample, null);
    assertEquals(idExample, example.getIdExample());
    }

    @Test
    void getExampleText() {
        String exampleText = "Example Text";
        Example example = new Example(null, exampleText);
        assertEquals(exampleText, example.getExampleText());
    }
}
