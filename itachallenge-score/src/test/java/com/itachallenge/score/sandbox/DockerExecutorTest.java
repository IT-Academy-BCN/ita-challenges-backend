// DockerExecutorTest.java
package com.itachallenge.score.sandbox;

import com.itachallenge.score.util.ExecutionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@ExtendWith(MockitoExtension.class)
public class DockerExecutorTest {

    @InjectMocks
    private DockerExecutor dockerExecutor;

    private GenericContainer<?> javaContainer;

    @BeforeEach
    public void setUp() {
        javaContainer = new GenericContainer<>(DockerImageName.parse("openjdk:21"))
                .withCommand("sh", "-c", "while true; do sleep 1000; done"); // Infinite loop to keep the container running
        javaContainer.start();
    }

    @Test
    public void testExecuteWithValidCode() throws IOException, InterruptedException {
        String javaCode = "System.out.println(99);";
        ExecutionResult result = dockerExecutor.execute(javaCode);

        assertTrue(result.isCompiled());
        assertTrue(result.isExecution());
        assertEquals("99", result.getMessage());
    }

    @Test
    public void testExecuteWithNullCode() throws IOException, InterruptedException {
        ExecutionResult result = dockerExecutor.execute(null);

        assertFalse(result.isCompiled());
        assertFalse(result.isExecution());
        assertEquals("Java code is null", result.getMessage());
    }

    @Test
    public void testExecuteWithLongCode() throws IOException, InterruptedException {
        String code =
                "String input = \"example\"; " +
                        "char[] charArray = input.toCharArray(); " +
                        "int n = charArray.length;" +
                        " for (int i = 0; i < n - 1; i++) { " +
                        "for (int j = 0; j < n - i - 1; j++) { " +
                        "if (charArray[j] > charArray[j + 1]) {" +
                        " char temp = charArray[j]; " +
                        "charArray[j] = charArray[j + 1]; " +
                        "charArray[j + 1] = temp; " +
                        "} " +
                        "} " +
                        "} " +
                        "System.out.println(new String(charArray));";

        ExecutionResult result = dockerExecutor.execute(code);

        // Verificación del resultado
        assertTrue(result.isCompiled(), "Is compiled: " + result.getMessage());
        assertTrue(result.isExecution(), "Is executed: " + result.getMessage());
        assertEquals("aeelmpx", result.getMessage());
    }

    @Test
    public void testExecuteWithComplexCode() throws IOException, InterruptedException {
        String code =
                "int n = 1000000; " +
                        "boolean[] isPrime = new boolean[n + 1]; " +
                        "for (int i = 2; i <= n; i++) isPrime[i] = true; " +
                        "for (int p = 2; p * p <= n; p++) { " +
                        "if (isPrime[p]) { " +
                        "for (int i = p * p; i <= n; i += p) isPrime[i] = false; " +
                        "} " +
                        "} " +
                        "StringBuilder primes = new StringBuilder(); " +
                        "for (int i = 2; i <= n; i++) { " +
                        "if (isPrime[i]) primes.append(i).append(\",\"); " +
                        "} " +
                        "System.out.println(primes.toString()); ";
        ExecutionResult result = dockerExecutor.execute(code);

        assertTrue(result.isCompiled(), "Is compiled: " + result.getMessage());
        assertTrue(result.isExecution(), "Is executed: " + result.getMessage());
        assertTrue(result.getMessage().contains("2,3,5,7,11,13"), "Output contains prime numbers: " + result.getMessage());
    }

    @Test
    public void testExecuteWithInfiniteLoop() throws IOException, InterruptedException {
        String javaCode = "while(true) {}";
        ExecutionResult result = dockerExecutor.execute(javaCode);

        assertFalse(result.isCompiled());
        assertFalse(result.isExecution());
        assertTrue(result.getMessage().contains("Execution timed out"));
    }

    @Test
    public void testExecuteWithSyntaxError() throws IOException, InterruptedException {
        String javaCode = "System.out.println(\"Hello World\"";
        ExecutionResult result = dockerExecutor.execute(javaCode);

        assertFalse(result.isCompiled());
        assertFalse(result.isExecution());
        assertTrue(result.getMessage().contains("Execution failed"));
    }

    @DisplayName("Test cleanUpContainers method")
    @Test
    public void testCleanUpContainers() throws IOException, InterruptedException {

        dockerExecutor.cleanUpContainers("java-executor-container");

        Process listContainersProcess = new ProcessBuilder("docker", "ps", "-a", "-q", "--filter", "name=java-executor-container").start();
        BufferedReader containerIdReader = new BufferedReader(new InputStreamReader(listContainersProcess.getInputStream()));
        assertNull(containerIdReader.readLine());
    }

    @DisplayName("Test execute with IOException")
    @Test
    public void testExecuteWithIOException() throws IOException, InterruptedException {
        String javaCode = "System.out.println(99);";


        DockerExecutor dockerExecutorMock = Mockito.spy(dockerExecutor);
        Mockito.doThrow(new IOException("Execution failed")).when(dockerExecutorMock).execute(javaCode);

        assertThrows(IOException.class, () -> {
            dockerExecutorMock.execute(javaCode);
        });
    }

    @DisplayName("Test execute with InterruptedException")
    @Test
    public void testExecuteWithInterruptedException() throws IOException, InterruptedException {
        String javaCode = "System.out.println(99);";


        DockerExecutor dockerExecutorMock = Mockito.spy(dockerExecutor);
        Mockito.doThrow(new InterruptedException("Execution interrupted")).when(dockerExecutorMock).execute(javaCode);

        assertThrows(InterruptedException.class, () -> {
            dockerExecutorMock.execute(javaCode);
        });
    }
}