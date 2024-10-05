package com.itachallenge.score.exception;

import com.itachallenge.score.exception.DockerExecutionException;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

class DockerExecutionExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Execution failed";
        DockerExecutionException exception = new DockerExecutionException(message, null);
        assertEquals(message, exception.getMessage());
    }

}