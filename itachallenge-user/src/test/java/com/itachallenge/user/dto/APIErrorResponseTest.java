package com.itachallenge.user.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
