package com.itachallenge.score.sandbox.sandbox_filter;

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

    @Autowired
    private JavaSandboxContainer javaSandboxContainer;



    @Override
    public ExecutionResultDto apply(String code, String resultExpected) {


        GenericContainer<?> sandboxContainer = javaSandboxContainer.getContainer();
        if (!sandboxContainer.isRunning()) {
            javaSandboxContainer.startContainer();
        }

        log.info("Código to execute:\n{}", code);

        try {
            String codeFilePath = "/app/Main.java";
            javaSandboxContainer.copyFileToContainer(sandboxContainer, code, codeFilePath);

            // Execute the code in the sandbox container
            String compileCommand = "javac /app/Main.java";  // Compile the Main.java file
            String runCommand = "java -cp /app Main";  // Execute the Main class

            javaSandboxContainer.executeCommand(sandboxContainer, "sh", "-c", compileCommand);
            Container.ExecResult execResult = sandboxContainer.execInContainer("sh", "-c", runCommand);

            ExecutionResultDto executionResultDto = new ExecutionResultDto();


            if (execResult.getExitCode() != 0) {
                executionResultDto.setCompiled(true);
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
