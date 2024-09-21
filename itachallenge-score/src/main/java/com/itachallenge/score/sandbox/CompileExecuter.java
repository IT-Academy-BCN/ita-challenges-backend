package com.itachallenge.score.sandbox;

import com.itachallenge.score.util.ExecutionResult;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class CompileExecuter {

    private static final Logger log = getLogger(CompileExecuter.class.getName());
    private JavaSandboxContainer javaSandboxContainer;

    public CompileExecuter(JavaSandboxContainer javaSandboxContainer) {
        this.javaSandboxContainer = javaSandboxContainer;
    }

    @Value("${code.execution.template}")
    private String codeTemplate;

    @Value("${code.execution.filepath:/app/Main.java}")
    private String codeFilePath;

    public ExecutionResult executeCode(String code) {

        GenericContainer<?> sandboxContainer = javaSandboxContainer.getContainer();
        if (!sandboxContainer.isRunning()) {
            javaSandboxContainer.startContainer();
        }

        // Complete the code with the template
        String completeCode = String.format(codeTemplate, code);
        log.info("Executing code:\n {}", completeCode);

        try {
            javaSandboxContainer.copyFileToContainer(sandboxContainer, completeCode, codeFilePath);

            String compileCommand = "javac " + codeFilePath;
            Container.ExecResult compileResult = sandboxContainer.execInContainer("sh", "-c", compileCommand);

            ExecutionResult executionResult = new ExecutionResult();

            if (compileResult.getExitCode() != 0) {
                executionResult.setCompiled(false);
                executionResult.setExecution(false);
                executionResult.setMessage(compileResult.getStderr().trim());
                return executionResult;
            }

            // Execute the code
            String runCommand = "java -cp /app Main";
            Container.ExecResult execResult = sandboxContainer.execInContainer("sh", "-c", runCommand);

            if (execResult.getExitCode() != 0) {
                executionResult.setCompiled(true);
                executionResult.setExecution(false);
                executionResult.setMessage(execResult.getStderr().trim());
            } else {
                executionResult.setCompiled(true);
                executionResult.setExecution(true);
                executionResult.setMessage(execResult.getStdout().trim());
            }

            return executionResult;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); 
            log.error("Thread was interrupted", e);
            throw new RuntimeException("Thread was interrupted", e);
        } catch (Exception e) {
            log.error("Error executing code", e);

            ExecutionResult executionResult = new ExecutionResult();
            executionResult.setCompiled(false);
            executionResult.setExecution(false);
            executionResult.setMessage("Error: " + e.getMessage().trim());
            return executionResult;

        } finally {
            javaSandboxContainer.stopContainer();
        }
    }
}