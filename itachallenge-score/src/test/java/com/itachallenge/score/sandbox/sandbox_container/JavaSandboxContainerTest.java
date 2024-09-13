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

import static org.junit.jupiter.api.Assertions.*;

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

@Test
void testExecuteMaliciousCommand() throws IOException, InterruptedException {
    JavaSandboxContainer container = new JavaSandboxContainer();
    container.startContainer();

    // Ensure the /tmp/app directory exists
    container.getContainer().execInContainer("mkdir", "-p", "/tmp/app");

    // Create a Java code snippet with malicious code
    String maliciousCode = "import java.io.*;\n" +
                           "public class Main { \n" +
                           "    public static void main(String[] args) { \n" +
                           "        int numero = 2345;\n" +
                           "        int[] conteoDigitos = new int[10];\n" +
                           "        while (numero > 0) {\n" +
                           "            int digito = numero % 10;\n" +
                           "            conteoDigitos[digito]++;\n" +
                           "            numero /= 10;\n" +
                           "        }\n" +
                           "        int resultado = 0;\n" +
                           "        for (int i = 9; i >= 0; i--) {\n" +
                           "            while (conteoDigitos[i] > 0) {\n" +
                           "                resultado = resultado * 10 + i;\n" +
                           "                conteoDigitos[i]--;\n" +
                           "            }\n" +
                           "        }\n" +
                           "        System.out.println(99);\n" +
                           "        // Malicious code\n" +
                           "        try { \n" +
                           "            File file = new File(\"/tmp/malicious.txt\");\n" +
                           "            FileWriter writer = new FileWriter(file);\n" +
                           "            writer.write(\"This is a malicious file.\");\n" +
                           "            writer.close();\n" +
                           "        } catch (IOException e) { \n" +
                           "            e.printStackTrace();\n" +
                           "        }\n" +
                           "    }\n" +
                           "}";

    String mainClassPath = "/tmp/app/Main.java";
    container.copyFileToContainer(container.getContainer(), maliciousCode, mainClassPath);

    // Compile the Main class inside the container using CustomClassLoader
    GenericContainer<?> javaContainer = container.getContainer();
    Container.ExecResult compileResult = javaContainer.execInContainer("javac", "-cp", "/path/to/custom/classloader", mainClassPath);
    assertEquals(0, compileResult.getExitCode(), "Compilation should be successful");

    // Execute the Main class
    Container.ExecResult result = javaContainer.execInContainer("java", "-cp", "/tmp/app:/path/to/custom/classloader", "Main");
    String output = result.getStdout().trim();
    assertEquals("99", output, "Command output should match");

    // Verify the malicious file is not created
    Container.ExecResult fileCheckResult = javaContainer.execInContainer("ls", "/tmp/malicious.txt");
    assertEquals(1, fileCheckResult.getExitCode(), "Malicious file should not be created");

    // Check for prohibited classes
    List<String> prohibitedClasses = JavaSandboxContainer.getProhibitedClasses();
    for (String prohibitedClass : prohibitedClasses) {
        if (maliciousCode.contains(prohibitedClass)) {
            container.stopContainer();
            assertFalse(container.getContainer().isRunning(), "Container should be stopped due to prohibited class usage");
            assertThrows(ClassNotFoundException.class, () -> {
                new CustomClassLoader(ClassLoader.getSystemClassLoader(), prohibitedClasses).loadClass(prohibitedClass);
            }, "ClassNotFoundException should be thrown for prohibited class " + prohibitedClass);
            return;
        }
    }

    // Stop the container
    container.stopContainer();
}
}