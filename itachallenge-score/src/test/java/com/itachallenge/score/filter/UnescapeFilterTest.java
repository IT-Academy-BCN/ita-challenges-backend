package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResultDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        filter.apply(escapedInput, null);

        assertEquals(expectedOutput, MockFilter.lastInput, "The unescaped code matches the expected output");
    }


    @DisplayName("Test unescape with unsupported escape sequence")
    @Test
    void testUnescapeWithUnsupportedEscape() {
        UnescapeFilter filter = new UnescapeFilter();
        Filter nextFilter = mock(Filter.class);
        filter.setNext(nextFilter);


        String escapedInput = "Hello \\u00AEworld";
        String expectedOutput = "Hello \\u00AEworld"; // Same as input because the escape sequence is not supported


        filter.apply(escapedInput, null);

        // Verify that the input was not modified
        assertEquals(expectedOutput, MockFilter.lastInput, "The unescaped code should match the expected output with unsupported escape sequence");

        // Verify that the next filter was not called
        verify(nextFilter, never()).apply(anyString(), anyString());
    }

    static class MockFilter implements Filter {
        static String lastInput;

        @Override
        public ExecutionResultDto apply(String input, String codeExpected) {
            lastInput = input;
            return new ExecutionResultDto();
        }

        @Override
        public void setNext(Filter next) {}
    }
}
