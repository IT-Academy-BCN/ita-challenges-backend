package com.itachallenge.score.sandbox.sandbox_filter;

import com.itachallenge.score.dto.ExecutionResultDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AsciiFilterTest {

    String asciiValid = """
                  import java.util.Arrays;
                    
            public class Main {
                public static void main(String[] args) {
                    String numbers = "3,1,4,1,5,9";
                    int[] numArray = Arrays.stream(numbers.split(",")).mapToInt(Integer::parseInt).toArray();
                    Arrays.sort(numArray);
                    System.out.println(Arrays.toString(numArray));
                }
            }
                """;

    String asciiInvalid = """
                              import java.util.Arrays;
                    
            public class βain {
                public static void main(String[] args) {
                    String numbers = "©, 3,1,4,1,5,9";
                    int[] numArray = Arrays.stream(numbers.split(",")).mapToInt(Integer::parseInt).toArray();
                    Arrays.sort(numArray);
                    System.out.println(Arrays.toString(numArray)); """;



    @DisplayName("Test filter valid - Code contains only valid characters, next filter should be called")
    @Test
    void testFilterValid() {
        AsciiFilter filter = new AsciiFilter();
        MockFilter nextFilter = new MockFilter();
        filter.setNext(nextFilter);

        ExecutionResultDto result = filter.apply(asciiValid, null);

        // Verify that the result indicates success and the next filter was called
        assertEquals(asciiValid, MockFilter.lastInput, "The ASCII string should pass through the filter unchanged");
        assertEquals(true, result.isCompiled(), "The result should indicate that the code compiled successfully");
    }

    @DisplayName("Test filter invalid - Code contains invalid character, next filter should not be called")
    @Test
    void testFilterInvalid() {
        AsciiFilter filter = new AsciiFilter();
        Filter nextFilter = mock(Filter.class);
        filter.setNext(nextFilter);

        ExecutionResultDto result = filter.apply(asciiInvalid, null);

        String expectedMessage = "Invalid character 'β' in code";
        assertEquals(expectedMessage, result.getMessage(), "The result should contain the expected error message");
        assertEquals(false, result.isCompiled(), "The result should indicate that the code did not compile");

        // Verify that the next filter was not called
        verify(nextFilter, never()).apply(anyString(), anyString());
    }

    static class MockFilter implements Filter {
        static String lastInput;

        @Override
        public ExecutionResultDto apply(String input, String codeExpected) {
            lastInput = input;
            ExecutionResultDto result = new ExecutionResultDto();
            result.setCompiled(true);
            return result;
        }

        @Override
        public void setNext(Filter next) {
            // Do nothing
        }
    }
}