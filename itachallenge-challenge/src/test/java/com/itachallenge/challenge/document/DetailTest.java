package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DetailTest {

    UUID uuid_1 = UUID.fromString("8ecbfe54-fec8-11ed-be56-0242ac120002");
    UUID uuid_2 = UUID.fromString("26977eee-89f8-11ec-a8a3-0242ac120003");

    @Test
    void getDescription() {
        String description = "Test Description";
        DetailDocument detail = new DetailDocument(description, null, null);
        assertEquals(description, detail.getDescription());
    }

    @Test
    void getExamples() {
        List<ExampleDocument> examples = new ArrayList<>();
        examples.add(new ExampleDocument(uuid_1,"Example 1"));
        examples.add(new ExampleDocument(uuid_2,"Example 2"));
        DetailDocument detail = new DetailDocument(null, examples, null);
        assertEquals(examples, detail.getExamples());
    }

    @Test
    void getNote() {
        String note = "Test Note";
        DetailDocument detail = new DetailDocument(null, null, note);
        assertEquals(note, detail.getNote());
    }
}








