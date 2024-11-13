package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FilterChainSetupTest {


    @Autowired
    private FilterChainSetup filterChainSetup;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateFilterChain() {
        Filter filter = filterChainSetup.createFilterChain();

        assertNotNull(filter, "The filter chain should not be null");
        assertTrue(filter instanceof HtmlSanitizerFilter, "The first filter should be an HtmlSanitizerFilter");

        String inputCode = "asdf";

        ExecutionResult result = filter.apply(inputCode);

        assertNotNull(result, "The result should not be null");
    }
}