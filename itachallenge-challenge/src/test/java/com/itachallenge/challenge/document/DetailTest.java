package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DetailTest {

    UUID uuid_1 = UUID.fromString("8ecbfe54-fec8-11ed-be56-0242ac120002");
    UUID uuid_2 = UUID.fromString("26977eee-89f8-11ec-a8a3-0242ac120003");

    @Test
    void getDescription() {
        Map<Locale, String> descriptionMap = new HashMap<>();
            descriptionMap.put(Locale.forLanguageTag("ES"), "Descripción del Test");
            descriptionMap.put(Locale.forLanguageTag("CA"), "Descripció del Test");
            descriptionMap.put(Locale.ENGLISH, "Test Description");
        DetailDocument detail = new DetailDocument();
        assertEquals(descriptionMap, detail.getDescription());
    }

    @Test
    void getExamples() {
        List<ExampleDocument> examples = new ArrayList<>();
        Map<Locale, String> exampleMap1 = new HashMap<>();
            exampleMap1.put(Locale.forLanguageTag("ES"), "Ejemplo 1");
            exampleMap1.put(Locale.forLanguageTag("CA"), "Exemple 1");
            exampleMap1.put(Locale.ENGLISH, "Example 1");
        Map<Locale, String> exampleMap2 = new HashMap<>();
            exampleMap2.put(Locale.forLanguageTag("ES"), "Ejemplo 2");
            exampleMap2.put(Locale.forLanguageTag("CA"), "Exemple 2");
            exampleMap2.put(Locale.ENGLISH, "Example 2");
        examples.add(new ExampleDocument(uuid_1,exampleMap1));
        examples.add(new ExampleDocument(uuid_2,exampleMap2));
        DetailDocument detail = new DetailDocument();
        assertEquals(examples, detail.getExamples());
    }

    @Test
    void getNotes() {
        Map<Locale, String> notesMap = new HashMap<>();
            notesMap.put(Locale.forLanguageTag("ES"), "Nota de prueba");
            notesMap.put(Locale.forLanguageTag("CA"), "Nota de prova");
            notesMap.put(Locale.ENGLISH, "Test Note");
        DetailDocument detail = new DetailDocument();
        assertEquals(notesMap, detail.getNotes());
    }
}








