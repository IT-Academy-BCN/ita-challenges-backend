package com.itachallenge.score.sandbox;

import com.itachallenge.score.util.ExecutionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

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
                .withCommand("sh", "-c", "while true; do sleep 1000; done");
        javaContainer.start();
    }

    @Test
    public void testExecuteDockerCommandWithValidCode() throws IOException, InterruptedException {

        String javaCode = "System.out.println(99);";
        ExecutionResult result = dockerExecutor.executeDockerCommand(javaCode);


        assertTrue(result.isCompiled());
        assertTrue(result.isExecution());
        assertEquals("99", result.getMessage());
    }

    @Test
    public void testExecuteDockerCommandWithNullCode() throws IOException, InterruptedException {

        ExecutionResult result = dockerExecutor.executeDockerCommand(null);

        assertFalse(result.isCompiled());
        assertFalse(result.isExecution());
        assertEquals("Java code is null", result.getMessage());
    }

    @Test
    public void testExecuteDockerCommandWithInvalidCode() throws IOException, InterruptedException {

        String javaCode = "invalid code";


        ExecutionResult result = dockerExecutor.executeDockerCommand(javaCode);


        assertFalse(result.isCompiled());
        assertFalse(result.isExecution());
        assertTrue(result.getMessage().contains("Execution failed"));
    }

    @Test
    public void testExecuteDockerCommandWithLongRunningCode() throws IOException, InterruptedException {

        String javaCode = "while(true) {}";


        ExecutionResult result = dockerExecutor.executeDockerCommand(javaCode);

        assertFalse(result.isCompiled());
        assertFalse(result.isExecution());
        assertTrue(result.getMessage().contains("Execution timed out"));
    }

    @Test
    public void testExecuteDockerCommandWithSyntaxError() throws IOException, InterruptedException {

        String javaCode = "System.out.println(\"Hello World\"";


        ExecutionResult result = dockerExecutor.executeDockerCommand(javaCode);


        assertFalse(result.isCompiled());
        assertFalse(result.isExecution());
        assertTrue(result.getMessage().contains("Execution failed"));
    }

    @Test
    public void testExecuteDockerCommandWithInfiniteLoop() throws IOException, InterruptedException {

        String javaCode = "while(true) {}";
        ExecutionResult result = dockerExecutor.executeDockerCommand(javaCode);

        assertFalse(result.isCompiled());
        assertFalse(result.isExecution());
        assertTrue(result.getMessage().contains("Execution timed out"));
    }

    @BeforeEach
    public void tearDown() {
        if (javaContainer != null) {
            javaContainer.stop();
        }
    }
}
