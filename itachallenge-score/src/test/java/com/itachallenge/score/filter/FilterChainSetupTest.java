package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilterChainSetupTest {

    private FilterChainSetup filterChainSetup;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filterChainSetup = new FilterChainSetup();
    }

    @Test
    void testCreateFilterChain() {
        Filter filter = filterChainSetup.createFilterChain();

        assertNotNull(filter, "The filter chain should not be null");
        assertTrue(filter instanceof HtmlSanitizerFilter, "The first filter should be an HtmlSanitizerFilter");

        // Apply the filter chain to verify the order
        String inputCode = "step1";

        ExecutionResult result = filter.apply(inputCode);

        // Verify that the result is not null
        assertNotNull(result, "The result should not be null");
    }
}