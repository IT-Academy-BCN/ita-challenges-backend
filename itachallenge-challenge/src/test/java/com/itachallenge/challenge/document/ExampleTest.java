package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExampleTest {

    @Test
    void getIdExample() {
    UUID idExample = UUID.randomUUID();
    ExampleDocument example = new ExampleDocument(idExample, null);
    assertEquals(idExample, example.getIdExample());
}

    @Test
    void getExampleText() {
        String exampleText = "Example Text";
        ExampleDocument example = new ExampleDocument(null, exampleText);
        assertEquals(exampleText, example.getExampleText());
    }
}

