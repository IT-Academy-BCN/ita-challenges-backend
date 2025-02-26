package com.itachallenge.challenge.document;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DetailTest {

    @Test
    void getDescription() {
        String expectedDescription = "Description of the test";
        DetailDocument detail = new DetailDocument(expectedDescription);
        assertEquals(expectedDescription, detail.getDescription());
    }
}
