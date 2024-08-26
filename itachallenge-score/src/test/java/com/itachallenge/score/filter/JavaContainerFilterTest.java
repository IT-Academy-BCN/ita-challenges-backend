package com.itachallenge.score.filter;

import com.itachallenge.score.docker.JavaSandboxContainer;
import com.itachallenge.score.dto.ExecutionResultDto;
import org.junit.jupiter.api.BeforeEach;
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
        javaContainerFilter = new JavaContainerFilter();
        javaContainerFilter.setJavaSandboxContainer(javaSandboxContainer);
        nextFilter = mock(Filter.class);
        javaContainerFilter.setNext(nextFilter);
    }

    @Test
    void testApply_withValidCode() {
        String validCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";
        String expectedCode = "Hello, World!";
        when(nextFilter.apply(validCode, expectedCode)).thenReturn(new ExecutionResultDto());


        ExecutionResultDto result = javaContainerFilter.apply(validCode, expectedCode);


        verify(nextFilter).apply(validCode, expectedCode);
        verify(javaSandboxContainer).startContainer();
    }



    @Test
    void testApply_withContainerStartException() {
        String validCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";

        // Simular una excepción al iniciar el contenedor
        doThrow(new RuntimeException("Container start failed")).when(javaSandboxContainer).startContainer();

        ExecutionResultDto result = javaContainerFilter.apply(validCode, "expectedCode");

        // Verificar que el mensaje de error contiene la excepción
        assertTrue(result.getMessage().contains("Error starting sandbox container"), "Error message should indicate container start failure");

        // Verificar que el siguiente filtro no fue llamado
        verify(nextFilter, never()).apply(anyString(), anyString());
    }
}
