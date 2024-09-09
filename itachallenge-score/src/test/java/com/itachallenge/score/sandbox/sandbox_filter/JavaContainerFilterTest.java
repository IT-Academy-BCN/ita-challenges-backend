package com.itachallenge.score.sandbox.sandbox_filter;

import com.itachallenge.score.sandbox.sandboxContainer.JavaSandboxContainer;
import com.itachallenge.score.dto.ExecutionResultDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class JavaContainerFilterTest {

    private JavaContainerFilter javaContainerFilter;
    private JavaSandboxContainer javaSandboxContainer;
    private Filter nextFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        javaSandboxContainer = mock(JavaSandboxContainer.class);
        javaContainerFilter = new JavaContainerFilter(javaSandboxContainer);
        nextFilter = mock(Filter.class);
        javaContainerFilter.setNext(nextFilter);
    }

    @DisplayName("Test apply - Code contains only valid characters, next filter should be called")
    @Test
    void testApply_withValidCode() {
        String validCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";
        String expectedCode = "Hello, World!";
        when(nextFilter.apply(validCode, expectedCode)).thenReturn(new ExecutionResultDto());

        ExecutionResultDto result = javaContainerFilter.apply(validCode, expectedCode);

        // Verify that the next filter was called
        verify(nextFilter).apply(validCode, expectedCode);

        // Verify that the result indicates success
        verify(javaSandboxContainer).startContainer();
        assertTrue(result.isCompiled(), "The result should indicate that the code compiled successfully");
    }


    @DisplayName("Test apply - Code contains invalid characters, next filter should not be called")
    @Test
    void testApply_withContainerStartException() {
        String validCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";

        // Mock the container to throw an exception when startContainer is called
        doThrow(new RuntimeException("Container start failed")).when(javaSandboxContainer).startContainer();

        ExecutionResultDto result = javaContainerFilter.apply(validCode, "expectedCode");

        // Verify that the result indicates failure
        assertTrue(result.getMessage().contains("Error starting sandbox container"), "Error message should indicate container start failure");

        // Verify that the next filter was not called
        verify(nextFilter, never()).apply(anyString(), anyString());
    }
}