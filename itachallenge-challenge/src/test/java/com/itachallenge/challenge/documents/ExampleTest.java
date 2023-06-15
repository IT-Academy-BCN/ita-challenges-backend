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

}
