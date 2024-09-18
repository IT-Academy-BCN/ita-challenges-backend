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

    @Value("${code.execution.template}")
    private String codeTemplate;

    @Override
    public ExecutionResultDto apply(String code, String resultExpected) {
        GenericContainer<?> sandboxContainer = javaSandboxContainer.getContainer();
        if (!sandboxContainer.isRunning()) {
            javaSandboxContainer.startContainer();
        }


        String completeCode = String.format(codeTemplate, code);
        log.info("Executing code:\n {}", completeCode);

        try {
            String codeFilePath = "/app/Main.java";
            javaSandboxContainer.copyFileToContainer(sandboxContainer, completeCode, codeFilePath);

            String compileCommand = "javac " + codeFilePath;
            Container.ExecResult compileResult = sandboxContainer.execInContainer("sh", "-c", compileCommand);

            ExecutionResultDto executionResultDto = new ExecutionResultDto();


            if (compileResult.getExitCode() != 0) {
                executionResultDto.setCompiled(false);
                executionResultDto.setExecution(false);
                executionResultDto.setMessage("Compiled error: \n" + compileResult.getStderr());
                return executionResultDto;
            }

            // If the code is compiled, execute it
            String runCommand = "java -cp /app Main";
            Container.ExecResult execResult = sandboxContainer.execInContainer("sh", "-c", runCommand);


            if (execResult.getExitCode() != 0) {
                executionResultDto.setCompiled(true);
                executionResultDto.setExecution(false);
                executionResultDto.setMessage("Execution error: " + execResult.getStderr());
            } else {
                executionResultDto.setCompiled(true);
                executionResultDto.setExecution(true);
                executionResultDto.setMessage(execResult.getStdout().trim());
            }

            return executionResultDto;

        } catch (Exception e) {
            log.error("Error executing code", e);

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
