package com.itachallenge.score.sandbox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        assertNotNull(newContainer, "Container should be created");
    }

    @Test
    void testJavaContainerSortNumbers() {
        String codeSort = """
                import java.util.Arrays;

                public class Main {
                    public static void main(String[] args) {
                        String numbers = "3,1,4,1,5,9";
                        int[] numArray = Arrays.stream(numbers.split(",")).mapToInt(Integer::parseInt).toArray();
                        Arrays.sort(numArray);
                        System.out.println(Arrays.toString(numArray));
                    }
                }
                """;

        GenericContainer<?> containerJavaSort = container.createContainer("openjdk:11");

        try {
            containerJavaSort.start(); // Ensure the container is started
            container.copyFileToContainer(containerJavaSort, codeSort, "/app/Main.java");
            container.executeCommand(containerJavaSort, "javac", "/app/Main.java");
            String output = containerJavaSort.execInContainer("java", "-cp", "/app", "Main").getStdout().trim();

            assertTrue(output.contains("[1, 1, 3, 4, 5, 9]"));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            container.stopContainer(containerJavaSort);
        }
    }

    @Test
    void testJavaContainerCompileError() {
        String codeError = """
                          public class Main {
                          public static void main(String[] args) {
                              System.out.println("Esto no compila porque falta un paréntesis";
                          }
                      }
                """;

        GenericContainer<?> containerJavaError = container.createContainer("openjdk:11");

        try {
            containerJavaError.start(); // Ensure the container is started
            container.copyFileToContainer(containerJavaError, codeError, "/app/Main.java");
            container.executeCommand(containerJavaError, "javac", "/app/Main.java");
        } catch (IOException | InterruptedException e) {
            String errorMessage = e.getMessage();
            assertTrue(errorMessage.contains("error: ';' expected"));
        } finally {
            container.stopContainer(containerJavaError);
        }
    }

    @Test
    void testRestrictedLibraryImport() {
        String code = """
                import java.lang.System;
                public class Main {
                    public static void main(String[] args) {
                        System.out.println("Hola! Estoy intentado importar System");
                    }
                }
                """;

        String code2 = """
                import java.util.Scanner;
                public class Main {
                    public static void main(String[] args) {
                        Scanner scanner = new Scanner(System.in);
                        System.out.println("Hola! Estoy intentado importar Scanner");
                    }
                }
                """;
        assertFalse(CustomClassLoader.isLibraryImportAllowed(code)); // Debería devolver false al intentar importar java.lang.System
        assertTrue(CustomClassLoader.isLibraryImportAllowed(code2)); // Debería devolver true al intentar importar java.util.Scanner

        // Si el código no importa java.lang.System, entonces se intenta compilar
if (CustomClassLoader.isLibraryImportAllowed(code)) {
    GenericContainer<?> javaContainer = this.container.createContainer("openjdk:11");

    try {
        this.container.copyFileToContainer(javaContainer, code, "/app/Main.java");
    } catch (IOException e) {
        fail("IOException occurred while copying file to container: " + e.getMessage());
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }

    try {
        this.container.executeCommand(javaContainer, "javac", "/app/Main.java");
        fail("Expected a compilation error, but the code compiled successfully");
    } catch (IOException | InterruptedException e) {
        assertTrue(e.getMessage().contains("error: package java.lang.System does not exist"));
    } finally {
        this.container.stopContainer(javaContainer);
    }
}
    }

}