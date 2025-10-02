package com.itachallenge.user.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class APIErrorResponseTest {

    @Test
    void testConstructorAndGetters() {
        Instant now = Instant.now();
        APIErrorResponse errorResponse = new APIErrorResponse("Some error", "Something went wrong", HttpStatus.NOT_FOUND, "http://localhost:8762/api/v1/user");

        assertEquals("Some error", errorResponse.getError());
        assertEquals("Something went wrong", errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("http://localhost:8762/api/v1/user", errorResponse.getPath());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        Instant now = Instant.now();
        APIErrorResponse errorResponse = new APIErrorResponse();
        errorResponse.setError("Another error");
        errorResponse.setMessage("Different message");
        errorResponse.setTimestamp(now);
        errorResponse.setStatus(404);
        errorResponse.setPath("http://localhost:8762/api/v1/user");

        assertEquals("Another error", errorResponse.getError());
        assertEquals("Different message", errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
        assertEquals(404, errorResponse.getStatus());
        assertEquals("http://localhost:8762/api/v1/user", errorResponse.getPath());
    }
}
