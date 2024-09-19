package com.itachallenge.score.sandbox.sandbox_filter;

import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.filter.Filter;
import com.itachallenge.score.filter.FilterChainSetup;
import com.itachallenge.score.filter.UnescapeFilter;
import com.itachallenge.score.sandbox.CompileExecuter;
import com.itachallenge.score.sandbox.JavaSandboxContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        Filter filter = filterChainSetup.createFilterChain( compileExecuter, javaSandboxContainer);

        assertNotNull(filter, "The filter chain should not be null");
        assertTrue(filter instanceof UnescapeFilter, "The first filter should be an UnescapeFilter");

        // Mock the behavior of the filters
        when(javaContainer.apply("step1", "step2")).thenReturn(new ExecutionResultDto(true, "step3"));


        // Apply the filter chain to verify the order
        String inputCode = "step1";

        ExecutionResultDto result = filter.apply(inputCode, "step2");

        // Verify that the result is not null
        assertNotNull(result, "The result should not be null");
    }
}