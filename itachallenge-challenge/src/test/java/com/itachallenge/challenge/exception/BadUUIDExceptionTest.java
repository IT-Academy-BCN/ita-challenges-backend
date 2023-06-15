package com.itachallenge.challenge.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BadUUIDExceptionTest {

    @Test
    void testBadUUIDExceptionClass() {
        String message = "Invalid UUID";
        BadUUIDException exception = new BadUUIDException(message);

        assertEquals(message, exception.getMessage());
    }
}
