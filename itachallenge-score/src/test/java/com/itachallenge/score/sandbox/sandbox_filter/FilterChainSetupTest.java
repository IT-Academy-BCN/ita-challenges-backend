package com.itachallenge.score.sandbox.sandbox_filter;

import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.sandbox.sandbox_container.JavaSandboxContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilterChainSetupTest {

    private FilterChainSetup filterChainSetup;
    private JavaContainerFilter javaContainerFilter;
    private CompileExecuterFilter compileExecuterFilter;
    private JavaSandboxContainer javaSandboxContainer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        javaContainerFilter = mock(JavaContainerFilter.class);
        compileExecuterFilter = mock(CompileExecuterFilter.class);
        javaSandboxContainer = mock(JavaSandboxContainer.class);

        filterChainSetup = new FilterChainSetup();
    }

    @Test
    void testCreateFilterChain() {
        Filter filter = filterChainSetup.createFilterChain(javaContainerFilter, compileExecuterFilter, javaSandboxContainer);

        assertNotNull(filter, "The filter chain should not be null");
        assertTrue(filter instanceof UnescapeFilter, "The first filter should be an UnescapeFilter");

        // Mock the behavior of the filters
        when(javaContainerFilter.apply("step1", "step2")).thenReturn(new ExecutionResultDto(true, "step3"));


        // Apply the filter chain to verify the order
        String inputCode = "step1";

        ExecutionResultDto result = filter.apply(inputCode, "step2");

        // Verify that the result is not null
        assertNotNull(result, "The result should not be null");
    }
}