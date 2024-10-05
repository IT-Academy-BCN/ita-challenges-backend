package com.itachallenge.score.exception;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class ExecutionTimedOutExceptionTest {


    @Test
    void testExceptionMessage() {
        String message = "Execution timed out";
        ExecutionTimedOutException exception = new ExecutionTimedOutException(message);
        assertEquals(message, exception.getMessage());
    }

}
