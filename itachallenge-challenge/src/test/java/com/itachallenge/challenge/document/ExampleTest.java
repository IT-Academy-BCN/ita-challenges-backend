package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExampleTest {

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

