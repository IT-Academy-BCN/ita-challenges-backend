package com.itachallenge.score.sandbox.sandbox_filter;

import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.sandbox.sandbox_container.JavaSandboxContainer;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private JavaSandboxContainer javaSandboxContainer;

    @Value("${code.execution.template}")  // Inyección directa de la plantilla desde el yml
    private String codeTemplate;

    @Override
    public ExecutionResultDto apply(String code, String resultExpected) {
        GenericContainer<?> sandboxContainer = javaSandboxContainer.getContainer();
        if (!sandboxContainer.isRunning()) {
            javaSandboxContainer.startContainer();
        }

        log.info("Código recibido:\n{}", code);

        // Aplicar la plantilla al código del usuario
        String completeCode = String.format(codeTemplate, code);

        log.info("Código final a ejecutar:\n{}", completeCode);

        try {
            String codeFilePath = "/app/Main.java";
            javaSandboxContainer.copyFileToContainer(sandboxContainer, completeCode, codeFilePath);

            // Comando para compilar
            String compileCommand = "javac " + codeFilePath;
            Container.ExecResult compileResult = sandboxContainer.execInContainer("sh", "-c", compileCommand);

            ExecutionResultDto executionResultDto = new ExecutionResultDto();

            // Verificar si hay errores de compilación
            if (compileResult.getExitCode() != 0) {
                executionResultDto.setCompiled(false);
                executionResultDto.setExecution(false);
                executionResultDto.setMessage("Error de compilación: " + compileResult.getStderr());
                return executionResultDto;
            }

            // Comando para ejecutar el código si ha compilado correctamente
            String runCommand = "java -cp /app Main";
            Container.ExecResult execResult = sandboxContainer.execInContainer("sh", "-c", runCommand);

            // Manejo de la ejecución
            if (execResult.getExitCode() != 0) {
                executionResultDto.setCompiled(true);
                executionResultDto.setExecution(false);
                executionResultDto.setMessage("Error de ejecución: " + execResult.getStderr());
            } else {
                executionResultDto.setCompiled(true);
                executionResultDto.setExecution(true);
                executionResultDto.setMessage(execResult.getStdout());
            }

            return executionResultDto;

        } catch (Exception e) {
            log.error("Error compilando y ejecutando el código en el contenedor sandbox", e);

            ExecutionResultDto executionResultDto = new ExecutionResultDto();
            executionResultDto.setCompiled(false);
            executionResultDto.setExecution(false);
            executionResultDto.setMessage("Error: " + e.getMessage());
            return executionResultDto;
        } finally {
            javaSandboxContainer.stopContainer(sandboxContainer);
        }
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}
