package com.itachallenge.user.exception;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SolutionNotFoundExceptionTest {

    @Test
    void exceptionShouldContainProvidedMessage() {
        String message = "Solution not found";
        SolutionNotFoundException exception = new SolutionNotFoundException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void exceptionShouldHandleNullMessage() {
        SolutionNotFoundException exception = new SolutionNotFoundException(null);
        assertNull(exception.getMessage());
    }

    @Test
    void exceptionShouldBeInstanceOfRuntimeException() {
        SolutionNotFoundException exception = new SolutionNotFoundException("Solution not found");
        assertTrue(exception instanceof RuntimeException);
    }
}
