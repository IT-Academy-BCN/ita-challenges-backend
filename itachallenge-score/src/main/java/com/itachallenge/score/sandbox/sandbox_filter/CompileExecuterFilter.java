package com.itachallenge.score.sandbox.sandbox_filter;

import com.itachallenge.score.component.CodeExecutionService;
import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.sandbox.sandbox_container.JavaSandboxContainer;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;

import static org.slf4j.LoggerFactory.getLogger;

@Getter
@Setter
@Component
public class CompileExecuterFilter implements Filter {

    private static final Logger log = getLogger(CompileExecuterFilter.class.getName());

    private Filter next;
    private final CodeExecutionService codeExecutionService;

    @Autowired
    private JavaSandboxContainer javaSandboxContainer;

    public CompileExecuterFilter(CodeExecutionService codeExecutionService) {
        this.codeExecutionService = codeExecutionService;
    }

    @Override
    public ExecutionResultDto apply(String code, String resultExpected) {
        if (codeExecutionService == null) {
            throw new IllegalStateException("CodeExecutionService is not initialized");
        }

        // Verifica si el contenedor está corriendo
        GenericContainer<?> sandboxContainer = javaSandboxContainer.getContainer();
        if (!sandboxContainer.isRunning()) {
            javaSandboxContainer.startContainer();
        }

        // Log del código que se va a ejecutar
        log.info("Código a ejecutar:\n{}", code);
        log.info("Resultado esperado:\n{}", resultExpected);

        try {
            // Copiar el código fuente dentro del contenedor usando el método del JavaSandboxContainer
            String codeFilePath = "/app/Main.java";  // Ruta en el contenedor
            javaSandboxContainer.copyFileToContainer(sandboxContainer, code, codeFilePath);

            // Ejecutar el comando para compilar y correr el código en el contenedor
            String compileCommand = "javac /app/Main.java";  // Compilar la clase Main
            String runCommand = "java -cp /app Main";  // Ejecutar la clase Main

            log.info("Ejecutando comando de compilación: {}", compileCommand);
            javaSandboxContainer.executeCommand(sandboxContainer, "sh", "-c", compileCommand);

            log.info("Ejecutando comando de ejecución: {}", runCommand);
            Container.ExecResult execResult = sandboxContainer.execInContainer("sh", "-c", runCommand);

            ExecutionResultDto executionResultDto = new ExecutionResultDto();

            // Verificar si hubo errores de ejecución
            if (execResult.getExitCode() != 0) {
                executionResultDto.setCompiled(true);  // La compilación ha sido exitosa aunque haya errores en la ejecución
                executionResultDto.setExecution(false);
                executionResultDto.setMessage("Error: " + execResult.getStderr());
            } else {
                executionResultDto.setCompiled(true);
                executionResultDto.setExecution(true);
                executionResultDto.setMessage(execResult.getStdout());
            }

            return executionResultDto;

        } catch (Exception e) {
            log.error("Error compiling and executing code in sandbox container", e);

            ExecutionResultDto executionResultDto = new ExecutionResultDto();
            executionResultDto.setCompiled(false);
            executionResultDto.setExecution(false);
            executionResultDto.setMessage("Error compiling and executing code in sandbox container: " + e.getMessage());
            return executionResultDto;
        }
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}
