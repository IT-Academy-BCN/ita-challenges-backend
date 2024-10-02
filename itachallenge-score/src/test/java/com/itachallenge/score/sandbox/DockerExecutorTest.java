package com.itachallenge.score.sandbox;

import com.itachallenge.score.util.ExecutionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DockerExecutorTest {

    @InjectMocks
    private DockerExecutor dockerExecutor;

    private GenericContainer<?> javaContainer;

    @BeforeEach
    public void setUp() {
        // Inicializa el contenedor de Java 21 usando Testcontainers
        javaContainer = new GenericContainer<>(DockerImageName.parse("openjdk:21"))
                .withCommand("sh", "-c", "while true; do sleep 1000; done"); // Mantener el contenedor activo
        javaContainer.start();
    }

    @Test
    public void testExecuteDockerCommandWithValidCode() throws IOException, InterruptedException {
        // Código Java de prueba que será ejecutado dentro del contenedor
        String javaCode = "System.out.println(99);";

        // Ejecución del método
        ExecutionResult result = dockerExecutor.executeDockerCommand(javaCode);

        // Verificar que se compila y ejecuta correctamente
        assertTrue(result.isCompiled());
        assertTrue(result.isExecution());
        assertEquals("99", result.getMessage());
    }

    @Test
    public void testExecuteDockerCommandWithNullCode() throws IOException, InterruptedException {
        // Ejecución del método con un código null
        ExecutionResult result = dockerExecutor.executeDockerCommand(null);

        // Verificar que no se compila ni ejecuta, y que el mensaje de error es adecuado
        assertFalse(result.isCompiled());
        assertFalse(result.isExecution());
        assertEquals("Java code is null", result.getMessage());
    }

    @Test
    public void testExecuteDockerCommandWithInvalidCode() throws IOException, InterruptedException {
        // Código Java inválido que no debería compilarse
        String javaCode = "invalid code";

        // Ejecución del método
        ExecutionResult result = dockerExecutor.executeDockerCommand(javaCode);

        // Verificar que no se compila ni ejecuta, y que el mensaje de error es adecuado
        assertFalse(result.isCompiled());
        assertFalse(result.isExecution());
        assertTrue(result.getMessage().contains("Execution failed"));
    }

    @Test
    public void testExecuteDockerCommandWithLongRunningCode() throws IOException, InterruptedException {
        // Código Java que se ejecuta durante un tiempo prolongado
        String javaCode = "while(true) {}";

        // Ejecución del método
        ExecutionResult result = dockerExecutor.executeDockerCommand(javaCode);

        // Verificar que no se compila ni ejecuta, y que el mensaje de error es adecuado
        assertFalse(result.isCompiled());
        assertFalse(result.isExecution());
        assertTrue(result.getMessage().contains("Execution failed"));
    }

    @Test
    public void testExecuteDockerCommandWithSyntaxError() throws IOException, InterruptedException {
        // Código Java con error de sintaxis
        String javaCode = "System.out.println(\"Hello World\"";

        // Ejecución del método
        ExecutionResult result = dockerExecutor.executeDockerCommand(javaCode);

        // Verificar que no se compila ni ejecuta, y que el mensaje de error es adecuado
        assertFalse(result.isCompiled());
        assertFalse(result.isExecution());
        assertTrue(result.getMessage().contains("Execution failed"));
    }

    @Test
    public void testExecuteDockerCommandWithInfiniteLoop() throws IOException, InterruptedException {
        // Código Java con un bucle infinito
        String javaCode = "while(true) {}";

        // Ejecución del método
        ExecutionResult result = dockerExecutor.executeDockerCommand(javaCode);

        // Verificar que no se compila ni ejecuta, y que el mensaje de error es adecuado
        assertFalse(result.isCompiled());
        assertFalse(result.isExecution());
        assertTrue(result.getMessage().contains("Execution failed"));
    }

    @BeforeEach
    public void tearDown() {
        // Detener el contenedor
        if (javaContainer != null) {
            javaContainer.stop();
        }
    }
}
