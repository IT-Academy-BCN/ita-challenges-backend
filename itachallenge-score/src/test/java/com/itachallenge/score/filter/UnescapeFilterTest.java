package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UnescapeFilterTest {

    @DisplayName("Test unescape valid")
    @Test
    void testUnescapeValid() {
        UnescapeFilter filter = new UnescapeFilter();
        MockFilter nextFilter = new MockFilter();
        filter.setNext(nextFilter);

        String escapedInput = "Hello \\u003Cworld\\u003E";
        String expectedOutput = "Hello <world>";

        filter.apply(escapedInput);

        assertEquals(expectedOutput, MockFilter.lastInput, "The unescaped code matches the expected output");
    }

    @DisplayName("Test unescape with unsupported escape sequence")
    @Test
    void testUnescapeWithUnsupportedEscape() {
        UnescapeFilter filter = new UnescapeFilter();
        MockFilter nextFilter = new MockFilter();
        Filter nextFilterMock = mock(Filter.class);
        filter.setNext(nextFilter);

        String escapedInput = "Hello \\u00AAworld";
        String expectedOutput = "Hello \\u00AAworld";

        filter.apply(escapedInput);

        assertEquals(expectedOutput, MockFilter.lastInput, "The unescaped code matches the expected output");
        verify(nextFilterMock, never()).apply(anyString());
    }

    @DisplayName("Test unescape filter finished message")
    @Test
    void testUnescapeFilterFinishedMessage() {
        UnescapeFilter filter = new UnescapeFilter();

        String escapedInput = "Hello \\u003Cworld\\u003E";
        ExecutionResult result = filter.apply(escapedInput);

        assertEquals("UnescapeFilter: Finished unescaping", result.getMessage(), "The message should indicate the filter finished unescaping");
    }

    @Test
    void testUnescapeFilterNullInput() {
        UnescapeFilter filter = new UnescapeFilter();

        ExecutionResult result = filter.apply(null);

        assertEquals("UnescapeFilter error: Unescaped code is null", result.getMessage(), "The message should indicate the filter received a null input");
        assertFalse(result.isCompiled(), "The result should indicate the code was not compiled");
        assertFalse(result.isExecution(), "The result should indicate the code was not executed");
    }

    static class MockFilter implements Filter {
        static String lastInput;

        @Override
        public ExecutionResult apply(String input) {
            lastInput = input;
            ExecutionResult result = new ExecutionResult();
            result.setCompiled(true);
            result.setExecution(true);
            result.setMessage("MockFilter: Processed");
            return result;
        }

        @Override
        public void setNext(Filter next) {
            // Do nothing
        }
    }
}