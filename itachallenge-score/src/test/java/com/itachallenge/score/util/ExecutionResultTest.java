package com.itachallenge.score.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionResultTest {

    @Test
    void testConstructor() {
        ExecutionResult result = new ExecutionResult(true, true, true, "Compilation successful");
        assertTrue(result.isSuccess());
        assertTrue(result.isCompiled());
        assertTrue(result.isExecution());
        assertEquals("Compilation successful", result.getMessage());
    }

    @Test
    void testSettersAndGetters() {
        ExecutionResult result = new ExecutionResult();
        result.setSuccess(true);
        result.setCompiled(true);
        result.setExecution(true);
        result.setMessage("Test message");

        assertTrue(result.isSuccess());
        assertTrue(result.isCompiled());
        assertTrue(result.isExecution());
        assertEquals("Test message", result.getMessage());
    }
}