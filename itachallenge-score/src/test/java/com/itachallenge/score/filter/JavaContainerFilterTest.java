package com.itachallenge.score.filter;

import com.itachallenge.score.docker.DockerContainerHelper;
import com.itachallenge.score.docker.JavaSandboxContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.GenericContainer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class JavaContainerFilterTest {

    private JavaContainerFilter javaContainerFilter;
    private JavaSandboxContainer javaSandboxContainer;
    private Filter nextFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        javaContainerFilter = new JavaContainerFilter();
        nextFilter = mock(Filter.class);
        javaContainerFilter.setNext(nextFilter);
    }

    @Test
    void testApply_withValidCode() {

        String validCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";
        when(nextFilter.apply(validCode)).thenReturn(true);

        try (MockedStatic<DockerContainerHelper> mockedHelper = mockStatic(DockerContainerHelper.class)) {
            GenericContainer<?> mockedContainer = mock(GenericContainer.class);
            mockedHelper.when(javaSandboxContainer::startContainer).thenReturn(mockedContainer);


            boolean result = javaContainerFilter.apply(validCode);

            assertTrue(result, "Valid code should pass through JavaContainerFilter");
            verify(nextFilter).apply(validCode);
            mockedHelper.verify(javaSandboxContainer::startContainer);
        }
    }
}
