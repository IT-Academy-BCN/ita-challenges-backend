package com.itachallenge.score.sandbox.sandbox_container;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
class JavaSandboxContainerTest {

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
        container.startContainer();
        assertTrue(container.getContainer().isRunning(), "Container should be running");

        container.stopContainer();
        assertFalse(container.getContainer().isRunning(), "Container should be stopped");
    }

    @Test
    void testProhibitedClasses() {
        List<String> prohibitedClasses = JavaSandboxContainer.getProhibitedClasses();
        assertTrue(prohibitedClasses.contains("java.lang.System"), "Prohibited classes should contain java.lang.System");
        assertTrue(prohibitedClasses.contains("java.lang.Runtime"), "Prohibited classes should contain java.lang.Runtime");
        assertFalse(prohibitedClasses.contains("java.util.Scanner"), "Prohibited classes should not contain java.util.Scanner");
    }
}