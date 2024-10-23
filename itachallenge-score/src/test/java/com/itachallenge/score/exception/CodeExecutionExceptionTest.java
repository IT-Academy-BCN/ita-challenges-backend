package com.itachallenge.score.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeExecutionExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Execution failed";
        CodeExecutionException exception = new CodeExecutionException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testExceptionMessageAndCause() {
        String message = "Execution failed";
        Throwable cause = new Throwable("Root cause");
        CodeExecutionException exception = new CodeExecutionException(message, cause);
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}