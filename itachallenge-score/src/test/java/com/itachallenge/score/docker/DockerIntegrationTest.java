package com.itachallenge.score.docker;

import com.itachallenge.score.helper.CodeValidator;
import com.itachallenge.score.helper.DockerContainerHelper;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.Transferable;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class DockerIntegrationTest {

    @Test
    public void testJavaContainerSortNumbers() {

        String codeSort = "import java.util.Arrays;\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        String numbers = \"3,1,4,1,5,9\";\n" +
                "        int[] numArray = Arrays.stream(numbers.split(\",\")).mapToInt(Integer::parseInt).toArray();\n" +
                "        Arrays.sort(numArray);\n" +
                "        System.out.println(Arrays.toString(numArray));\n" +
                "    }\n" +
                "}";

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
    public void testJavaContainerCompileError() {

        String codeError = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Esto no compila porque falta un paréntesis\";\n" +
                "    }\n" +
                "}";

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
    public void testRestrictedLibraryImport() {
        String code = "import java.lang.System;\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"Hola! Estoy intentado importar System\");\n" +
                "    }\n" +
                "}";

        assertFalse(CodeValidator.isLibraryImportAllowed(code)); // Debería devolver false al intentar importar java.lang.System

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
