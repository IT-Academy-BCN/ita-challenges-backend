package com.itachallenge.score.sandbox.sandbox_filter;

import com.itachallenge.score.filter.Filter;
import com.itachallenge.score.sandbox.JavaSandboxContainer;
import com.itachallenge.score.dto.ExecutionResultDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class JavaContainerTest {

    private static final Logger log = LoggerFactory.getLogger(JavaContainerTest.class);
    private JavaContainer javaContainer;
    private JavaSandboxContainer javaSandboxContainer;
    private Filter nextFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        javaSandboxContainer = mock(JavaSandboxContainer.class);
        javaContainer = new JavaContainer(javaSandboxContainer);
        nextFilter = mock(Filter.class);
        javaContainer.setNext(nextFilter);
    }

    @DisplayName("Test apply - Code contains only valid characters, next filter should be called")
    @Test
    void testApply_withValidCode() {
        String validCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";
        String expectedCode = "Hello, World!";
        when(nextFilter.apply(validCode, expectedCode)).thenReturn(new ExecutionResultDto());

        ExecutionResultDto result = javaContainer.apply(validCode, expectedCode);
        log.info("Result: {}", result);

        // Verify that the next filter was called
        verify(nextFilter).apply(validCode, expectedCode);

        // Verify that the result indicates success
        verify(javaSandboxContainer).startContainer();

    }


    @DisplayName("Test apply - Code contains invalid characters, next filter should not be called")
    @Test
    void testApply_withContainerStartException() {
        String validCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";

        // Mock the container to throw an exception when startContainer is called
        doThrow(new RuntimeException("Container start failed")).when(javaSandboxContainer).startContainer();

        ExecutionResultDto result = javaContainer.apply(validCode, "expectedCode");

        // Verify that the result indicates failure
        assertTrue(result.getMessage().contains("Error starting sandbox container"), "Error message should indicate container start failure");

        // Verify that the next filter was not called
        verify(nextFilter, never()).apply(anyString(), anyString());
    }
}