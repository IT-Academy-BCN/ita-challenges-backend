package com.itachallenge.score.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExecutionResultTest {

    @Test
    void testConstructor() {
        ExecutionResult result = new ExecutionResult(true, "Compilation successful");
        assertTrue(result.isCompiled());
        assertEquals("Compilation successful", result.getMessage());
    }

    @Test
    void testIsResultCodeMatch() {
        ExecutionResult result = new ExecutionResult(true, "Compilation successful");
        result.setResultCodeMatch(true);
        assertTrue(result.isResultCodeMatch());
    }

    @Test
    void testSetResultCodeMatch() {
        ExecutionResult result = new ExecutionResult(true, "Compilation successful");
        result.setResultCodeMatch(false);
        assertFalse(result.isResultCodeMatch());
    }
}