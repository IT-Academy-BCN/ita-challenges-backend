package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResultDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AsciiFilterTest {

    String asciiValid = """
        String numbers = "3,1,4,1,5,9";
        int[] numArray = Arrays.stream(numbers.split(",")).mapToInt(Integer::parseInt).toArray();
        Arrays.sort(numArray);
        System.out.println(Arrays.toString(numArray));
    """;

    String asciiInvalid = """
        public class βain {
            public static void main(String[] args) {
                String numbers = "©, 3,1,4,1,5,9";
                int[] numArray = Arrays.stream(numbers.split(",")).mapToInt(Integer::parseInt).toArray();
                Arrays.sort(numArray);
                System.out.println(Arrays.toString(numArray));
            }
        }
    """;

    @DisplayName("Test filter valid - Code contains only valid characters, chain of filters done.")
    @Test
    void testFilterValid() {
        AsciiFilter filter = new AsciiFilter();
        MockFilter nextFilter = new MockFilter();
        filter.setNext(nextFilter);

        ExecutionResultDto result = filter.apply(asciiValid);

        assertEquals(asciiValid, MockFilter.lastInput, "The ASCII string should pass through the filter unchanged");
        assertEquals(true, result.isCompiled(), "The result should indicate that the code compiled successfully");
    }

    @DisplayName("Test filter invalid - Code contains invalid character")
    @Test
    void testFilterInvalid() {
        AsciiFilter filter = new AsciiFilter();
        Filter nextFilter = mock(Filter.class);
        filter.setNext(nextFilter);

        ExecutionResultDto result = filter.apply(asciiInvalid);

        String expectedMessage = "ASCII FILTER ERROR: Invalid character 'β' at index 17 in code";
        assertEquals(expectedMessage, result.getMessage(), "The result should contain the expected error message");
        assertEquals(false, result.isCompiled(), "The result should indicate that the code did not compile");

        verify(nextFilter, never()).apply(anyString());
    }

    @DisplayName("Test filter with empty code")
    @Test
    void testFilterEmptyCode() {
        AsciiFilter filter = new AsciiFilter();
        Filter nextFilter = mock(Filter.class);
        filter.setNext(nextFilter);

        ExecutionResultDto result = filter.apply("");

        assertEquals(false, result.isCompiled(), "The result should indicate the code is empty");
        assertEquals("Code is empty", result.getMessage(), "The result should contain the expected error message");
    }

    @DisplayName("Test filter with only special characters")
    @Test
    void testFilterOnlySpecialChars() {
        AsciiFilter filter = new AsciiFilter();
        Filter nextFilter = mock(Filter.class);
        filter.setNext(nextFilter);

        String specialChars = "áéíóúñÁÉÍÓÚÑ";
        ExecutionResultDto result = filter.apply(specialChars);

        verify(nextFilter).apply(specialChars);

    }

    @DisplayName("Test filter with mix of valid and invalid characters")
    @Test
    void testFilterMixValidInvalidChars() {
        AsciiFilter filter = new AsciiFilter();
        Filter nextFilter = mock(Filter.class);
        filter.setNext(nextFilter);

        String mixedChars = "Hello, World! β";
        ExecutionResultDto result = filter.apply(mixedChars);

        String expectedMessage = "ASCII FILTER ERROR: Invalid character 'β' at index 14 in code";
        assertEquals(expectedMessage, result.getMessage(), "The result should contain the expected error message");
        assertEquals(false, result.isCompiled(), "The result should indicate that the code did not compile");
        verify(nextFilter, never()).apply(anyString());
    }

    @DisplayName("Test filter with extended ASCII characters")
    @Test
    void testFilterExtendedAsciiChars() {
        AsciiFilter filter = new AsciiFilter();
        Filter nextFilter = mock(Filter.class);
        filter.setNext(nextFilter);

        String extendedAscii = "Hello, World! \u00A9";
        ExecutionResultDto result = filter.apply(extendedAscii);

        String expectedMessage = "ASCII FILTER ERROR: Invalid character '©' at index 14 in code";
        assertEquals(expectedMessage, result.getMessage(), "The result should contain the expected error message");
        assertEquals(false, result.isCompiled(), "The result should indicate that the code did not compile");
        verify(nextFilter, never()).apply(anyString());
    }

    static class MockFilter implements Filter {
        static String lastInput;

        @Override
        public ExecutionResultDto apply(String input) {
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