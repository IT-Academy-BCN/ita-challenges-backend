package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
        Map<Locale, String> exampleMap = new HashMap<>();
        exampleMap.put(Locale.ENGLISH, "Example Text");
        ExampleDocument example = new ExampleDocument(null, exampleMap);
        assertEquals(exampleMap, example.getExampleText());
    }
}

