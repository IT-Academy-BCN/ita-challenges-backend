package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Filter nextFilterMock = mock(Filter.class); // Create a mock filter only to verify that it was not called
        filter.setNext(nextFilter);

        String escapedInput = "Hello \\u00AAworld";
        String expectedOutput = "Hello \\u00AAworld"; //Same as input because the escape sequence is not supported

        filter.apply(escapedInput);

        assertEquals(expectedOutput, MockFilter.lastInput, "The unescaped code matches the expected output");

//        //Verify that the next filter was not called
           verify(nextFilterMock, never()).apply(anyString());
    }


    static class MockFilter implements Filter {
        static String lastInput;

        @Override
        public ExecutionResult apply(String input) {
            lastInput = input;
            return new ExecutionResult();
        }

        @Override
        public void setNext(Filter next) {
            // Do nothing
        }
    }
}
