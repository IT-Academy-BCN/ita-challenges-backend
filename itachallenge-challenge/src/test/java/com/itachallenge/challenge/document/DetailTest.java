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
        descriptionMap.put(Locale.ENGLISH, "Test Description");
        DetailDocument detail = new DetailDocument(descriptionMap, null, null);
        assertEquals(descriptionMap, detail.getDescription());
    }

    @Test
    void getExamples() {
        List<ExampleDocument> examples = new ArrayList<>();
        Map<Locale, String> exampleMap1 = new HashMap<>();
        Map<Locale, String> exampleMap2 = new HashMap<>();
        exampleMap1.put(Locale.ENGLISH, "Example 1");
        exampleMap2.put(Locale.ENGLISH, "Example 2");
        examples.add(new ExampleDocument(uuid_1,exampleMap1));
        examples.add(new ExampleDocument(uuid_2,exampleMap2));
        DetailDocument detail = new DetailDocument(null, examples, null);
        assertEquals(examples, detail.getExamples());
    }

    @Test
    void getNote() {
        Map<Locale, String> noteMap = new HashMap<>();
        noteMap.put(Locale.ENGLISH, "Test Note");
        DetailDocument detail = new DetailDocument(null, null, noteMap);
        assertEquals(noteMap, detail.getNote());
    }
}








