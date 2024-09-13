package com.itachallenge.score.sandbox.sandbox_container;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class JavaSandboxContainerTest {

    private static final Logger logger = LoggerFactory.getLogger(JavaSandboxContainerTest.class);
    private JavaSandboxContainer container;

    @BeforeEach
    void setUp() {
        container = new JavaSandboxContainer();
    }

    @AfterEach
    void tearDown() {
        if (container != null) {
            container.stopContainer();
        }
    }

    @Test
    void testContainerStartStop() {
        try {
            container.startContainer();
            assertTrue(container.getContainer().isRunning(), "Container should be running");
        } finally {
            container.stopContainer();
            assertFalse(container.getContainer().isRunning(), "Container should be stopped");
        }
    }

    @Test
    void testProhibitedClasses() {
        List<String> prohibitedClasses = JavaSandboxContainer.getProhibitedClasses();
        assertTrue(prohibitedClasses.contains("java.lang.System"), "Prohibited classes should contain java.lang.System");
        assertTrue(prohibitedClasses.contains("java.lang.Runtime"), "Prohibited classes should contain java.lang.Runtime");
        assertFalse(prohibitedClasses.contains("java.util.Scanner"), "Prohibited classes should not contain java.util.Scanner");
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
    void testCreateContainer() {
        GenericContainer<?> newContainer = container.createContainer("openjdk:11-jdk-slim-sid");
        assertTrue(newContainer != null, "Container should be created");
    }
}