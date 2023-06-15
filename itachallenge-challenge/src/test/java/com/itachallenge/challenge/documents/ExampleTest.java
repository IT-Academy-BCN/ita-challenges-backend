package com.itachallenge.challenge.documents;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ExampleTest {

    @Test
    public void testExampleClass() {
        // Arrange
        UUID idExample = UUID.fromString("d4656966-5277-48ce-9d13-4b41ef901722");
        String exampleText = "Example text";

        // Act
        Example example = Example.builder()
                .idExample(idExample)
                .exampleText(exampleText)
                .build();

        // Assert
        Assertions.assertEquals(idExample, example.getIdExample());
        Assertions.assertEquals(exampleText, example.getExampleText());
    }

    @Test
    public void testExampleDataMethods() {
        // Init data
        UUID exampleId = UUID.randomUUID();
        Example example = Example.builder()
                .idExample(exampleId)
                .exampleText("Texto de ejemplo")
                .build();

        // getter y setter
        Assertions.assertEquals(exampleId, example.getIdExample());
        Assertions.assertEquals("Texto de ejemplo", example.getExampleText());

        // toString()
        String expectedToString = "Example(idExample=" + exampleId + ", exampleText=Texto de ejemplo)";
        Assertions.assertEquals(expectedToString, example.toString());

        // equals()
        Example equalExample = Example.builder()
                .idExample(exampleId)
                .exampleText("Texto de ejemplo")
                .build();
        Assertions.assertEquals(example, equalExample);

        // hashCode()
        Assertions.assertEquals(example.hashCode(), equalExample.hashCode());
    }

}
