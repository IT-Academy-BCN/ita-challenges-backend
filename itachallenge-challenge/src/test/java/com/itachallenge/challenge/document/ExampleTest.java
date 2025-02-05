package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExampleTest {

    @Test
    void getId_example() {
    UUID id_example = UUID.randomUUID();
    ExampleDocument example = new ExampleDocument(id_example, null);
    assertEquals(id_example, example.getId_example());
}

    @Test
    void getExample_text() {
        Map<Locale, String> exampleMap = new HashMap<>();
            exampleMap.put(Locale.forLanguageTag("ES"), "Ejemplo de prueba");
            exampleMap.put(Locale.forLanguageTag("CA"), "Exemple de prova");
            exampleMap.put(Locale.ENGLISH, "Example Text");
        ExampleDocument example = new ExampleDocument(null, exampleMap);
        assertEquals(exampleMap, example.getExample_text());
    }
}

