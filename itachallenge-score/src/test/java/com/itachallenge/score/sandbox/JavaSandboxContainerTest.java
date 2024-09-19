package com.itachallenge.score.sandbox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

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
        Container.ExecResult result = container.getContainer().execInContainer("sh", "-c", command);

        assertEquals("Hello, World!", result.getStdout().trim(), "Command output should match");
    }

    @Test
    void testGetProhibitedClasses() {
        assertTrue(JavaSandboxContainer.getProhibitedClasses().contains("java.lang.System"), "Prohibited classes should contain java.lang.System");
    }
}