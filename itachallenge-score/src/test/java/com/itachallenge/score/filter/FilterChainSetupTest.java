package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResultDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilterChainSetupTest {

    @Test
    void testFilterChainSetup() {
        Filter filterChain = FilterChainSetup.createFilterChain();
        String expectedCode = "Hello, World!";

        String validCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";
        ExecutionResultDto validResult = filterChain.apply(validCode, expectedCode);
        assertTrue(validResult.isCompiled(), "The valid code should pass the filter chain");

        String invalidAsciiCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World! ✓\"); } }";
        ExecutionResultDto invalidAsciiResult = filterChain.apply(invalidAsciiCode, expectedCode);
        assertFalse(invalidAsciiResult.isCompiled(), "The code with non-ASCII characters should not pass the filter chain");

        String escapedCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, \\\"World\\\"\"); } }";
        ExecutionResultDto escapedResult = filterChain.apply(escapedCode, expectedCode);
        assertTrue(escapedResult.isCompiled(), "The escaped code should pass the filter chain after unescaping");
    }
}
