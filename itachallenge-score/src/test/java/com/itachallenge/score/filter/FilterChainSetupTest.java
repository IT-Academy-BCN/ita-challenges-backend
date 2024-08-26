package com.itachallenge.score.filter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilterChainSetupTest {

    @Test
    void testFilterChainSetup() {
        Filter filterChain = FilterChainSetup.createFilterChain();

        String validCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";
        assertTrue(filterChain.apply(validCode), "The valid code should pass the filter chain");

        String invalidAsciiCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World! ✓\"); } }";
        assertFalse(filterChain.apply(invalidAsciiCode), "The code with non-ASCII characters should not pass the filter chain");

        String escapedCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, \\\"World\\\"\"); } }";
        assertTrue(filterChain.apply(escapedCode), "The escaped code should pass the filter chain after unescaping");

    }
}
