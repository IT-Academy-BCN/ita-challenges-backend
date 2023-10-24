package com.itachallenge.user.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadUUIDExceptionTest {
    @Test
    public void testConstructorWithMessage() {
        String message = "Test message";
        BadUUIDException exception = new BadUUIDException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    public void testDefaultConstructor() {
        BadUUIDException exception = new BadUUIDException();

        assertNull(exception.getMessage());
    }
}