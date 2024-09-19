package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.sandbox.CompileExecuter;
import com.itachallenge.score.sandbox.JavaSandboxContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class FilterChainSetupTest {

    private FilterChainSetup filterChainSetup;
    private CompileExecuter compileExecuter;
    private JavaSandboxContainer javaSandboxContainer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        compileExecuter = mock(CompileExecuter.class);
        javaSandboxContainer = mock(JavaSandboxContainer.class);

        filterChainSetup = new FilterChainSetup();
    }

    @Test
    void testCreateFilterChain() {
        Filter filter = filterChainSetup.createFilterChain();

        assertNotNull(filter, "The filter chain should not be null");
        assertTrue(filter instanceof HtmlSanitizerFilter, "The first filter should be an HtmlSanitizerFilter");
        
        // Apply the filter chain to verify the order
        String inputCode = "step1";

        ExecutionResultDto result = filter.apply(inputCode);

        // Verify that the result is not null
        assertNotNull(result, "The result should not be null");
    }
}