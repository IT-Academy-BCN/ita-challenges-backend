package com.itachallenge.score.docker;

import com.itachallenge.score.helper.CodeValidator;
import com.itachallenge.score.helper.DockerContainerHelper;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Testcontainers
class DockerIntegrationTest {

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

        GenericContainer<?> containerJavaSort = DockerContainerHelper.createContainer("openjdk:11");

        try {
            DockerContainerHelper.copyFileToContainer(containerJavaSort, codeSort, "/app/Main.java");
            DockerContainerHelper.executeCommand(containerJavaSort, "javac", "/app/Main.java");
            String output = containerJavaSort.execInContainer("java", "-cp", "/app", "Main").getStdout().trim();


            assertTrue(output.contains("[1, 1, 3, 4, 5, 9]"));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            DockerContainerHelper.stopContainer(containerJavaSort);
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

        GenericContainer<?> containerJavaError = DockerContainerHelper.createContainer("openjdk:11");

        try {
            DockerContainerHelper.copyFileToContainer(containerJavaError, codeError, "/app/Main.java");
            DockerContainerHelper.executeCommand(containerJavaError, "javac", "/app/Main.java");
        } catch (IOException | InterruptedException e) {
            String errorMessage = e.getMessage();
            assertTrue(errorMessage.contains("error: ';' expected"));
        } finally {
            DockerContainerHelper.stopContainer(containerJavaError);
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
                }"
                """;

        String code2 = """
                import java.util.Scanner;
                public class Main {
                    public static void main(String[] args) {
                        Scanner scanner = new Scanner(System.in);
                        System.out.println("Hola! Estoy intentado importar Scanner");
                    }
                }"
                """;
        assertFalse(CodeValidator.isLibraryImportAllowed(code)); // Debería devolver false al intentar importar java.lang.System
        assertTrue(CodeValidator.isLibraryImportAllowed(code2)); // Debería devolver true al intentar importar java.util.Scanner

        // Si el código no importa java.lang.System, entonces se intenta compilar
        if (CodeValidator.isLibraryImportAllowed(code)) {
            GenericContainer<?> container = DockerContainerHelper.createContainer("openjdk:11");

            try {
                DockerContainerHelper.copyFileToContainer(container, code, "/app/Main.java");
                DockerContainerHelper.executeCommand(container, "javac", "/app/Main.java");

                fail("Expected a compilation error, but the code compiled successfully");
            } catch (IOException | InterruptedException e) {
                String errorMessage = e.getMessage();
                assertTrue(errorMessage.contains("error: package java.lang.System does not exist"));
            } finally {
                DockerContainerHelper.stopContainer(container);
            }
        }
    }
}