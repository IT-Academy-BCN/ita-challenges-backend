package com.itachallenge.score.sandbox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Testcontainers
class JavaSandboxContainerTest {

    private JavaSandboxContainer container;

    @BeforeEach
    void setUp() {
        container = new JavaSandboxContainer();
    }

    @AfterEach
    void tearDown() {
        container.stopContainer();
    }

    @Test
    void testCreateContainer() {
        GenericContainer<?> newContainer = container.createContainer("openjdk:11-jdk-slim-sid");
        assertNotNull(newContainer, "Container should be created");
    }

    @Test
    void testStartContainer() {
        container.startContainer();
        assertTrue(container.getContainer().isRunning(), "Container should be running");
    }

    @Test
    void testStopContainer() {
        container.startContainer();
        container.stopContainer();
        assertFalse(container.getContainer().isRunning(), "Container should be stopped");
    }

    @Test
    void testFailCreateContainer() {
        JavaSandboxContainer mockContainer = Mockito.mock(JavaSandboxContainer.class);
        doThrow(new RuntimeException("Mocked exception")).when(mockContainer).createContainer("invalid-image");

        assertThrows(RuntimeException.class, () -> {
            mockContainer.createContainer("invalid-image");
            mockContainer.startContainer();
        }, "Should throw exception");

        verify(mockContainer, times(1)).createContainer("invalid-image");
        verify(mockContainer, never()).startContainer();

    }

    @Test
    void testCopyFileToContainer() throws IOException, InterruptedException {
        container.startContainer();
        String fileContent = "Hello, World!";
        String containerPath = "/tmp/test.txt";

        container.copyFileToContainer(container.getContainer(), fileContent, containerPath);

        Container.ExecResult result = container.getContainer().execInContainer("cat", containerPath);
        String output = result.getStdout().trim();

        assertEquals(fileContent, output, "File content should match");
    }

    @Test
    void testExecuteCommand() throws IOException, InterruptedException {
        container.startContainer();
        String command = "echo Hello, World!";
        String failedCommand = "sh -c 'echo Hello, World! && exit 1'";

        // Execute the successful command
        Container.ExecResult result = container.getContainer().execInContainer("sh", "-c", command);
        assertEquals("Hello, World!", result.getStdout().trim(), "Command output should match");
        assertEquals("", result.getStderr().trim(), "Command error should be empty");

        // Execute the failing command
        Container.ExecResult failedResult = container.getContainer().execInContainer("sh", "-c", failedCommand);
        System.out.println("Failed command stderr: " + failedResult.getStderr().trim());
        assertEquals("Hello, World!", failedResult.getStdout().trim(), "Command output should match");
        assertEquals(1, failedResult.getExitCode(), "Command exit code should be 1");
    }


    @Test
    void testGetProhibitedClasses() {
        assertTrue(JavaSandboxContainer.getProhibitedClasses().contains("java.lang.System"), "Prohibited classes should contain java.lang.System");
    }
}