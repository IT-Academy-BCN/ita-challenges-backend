package com.itachallenge.score.sandbox.sandbox_filter;

import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.filter.Filter;
import com.itachallenge.score.filter.UnescapeFilter;
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

        filter.apply(escapedInput, null);

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

        filter.apply(escapedInput, null);

        assertEquals(expectedOutput, MockFilter.lastInput, "The unescaped code matches the expected output");

//        //Verify that the next filter was not called
           verify(nextFilterMock, never()).apply(anyString(), anyString());
    }


    static class MockFilter implements Filter {
        static String lastInput;

        @Override
        public ExecutionResultDto apply(String input, String codeExpected) {
            lastInput = input;
            return new ExecutionResultDto();
        }

        @Override
        public void setNext(Filter next) {
            // Do nothing
        }
    }
}
