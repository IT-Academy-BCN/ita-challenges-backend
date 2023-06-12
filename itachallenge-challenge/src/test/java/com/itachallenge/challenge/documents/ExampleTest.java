package com.itachallenge.challenge.documents;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExampleTest {

    @Test
    public void testExampleClass() {
        // Arrange
        String idExample = "123456";
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
