package com.itachallenge.user.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class APIErrorResponseTest {

    @Test
    void testConstructorAndGetters() {
        Instant now = Instant.now();
        APIErrorResponse errorResponse = new APIErrorResponse("Some error", "Something went wrong", now);

        assertEquals("Some error", errorResponse.getError());
        assertEquals("Something went wrong", errorResponse.getMessage());
        assertEquals(now, errorResponse.getTimestamp());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        Instant now = Instant.now();
        APIErrorResponse errorResponse = new APIErrorResponse();
        errorResponse.setError("Another error");
        errorResponse.setMessage("Different message");
        errorResponse.setTimestamp(now);

        assertEquals("Another error", errorResponse.getError());
        assertEquals("Different message", errorResponse.getMessage());
        assertEquals(now, errorResponse.getTimestamp());
    }

    @Test
    void testToString() {
        Instant now = Instant.now();
        APIErrorResponse errorResponse = new APIErrorResponse("Error", "Msg", now);

        String toString = errorResponse.toString();
        assertNotNull(toString);
        // Optionally check that the toString contains field values
        assert(toString.contains("Error"));
        assert(toString.contains("Msg"));
        assert(toString.contains(now.toString()));
    }
}
