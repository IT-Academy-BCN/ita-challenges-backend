package com.itachallenge.score.filter;

import com.itachallenge.score.dto.ExecutionResultDto;
import com.itachallenge.score.docker.JavaSandboxContainer;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testcontainers.containers.GenericContainer;

import static org.slf4j.LoggerFactory.getLogger;

@Getter
@Setter
@Component
public class JavaContainerFilter implements Filter {

    private static final Logger log = getLogger(JavaContainerFilter.class.getName());

    private Filter next;

    private JavaSandboxContainer javaSandboxContainer;

    @Getter
    private GenericContainer<?> sandboxContainer;

    @Autowired
    public JavaContainerFilter(JavaSandboxContainer javaSandboxContainer) {
        this.javaSandboxContainer = javaSandboxContainer;
    }

    @Override
    public ExecutionResultDto apply(String code, String resultExpected) {

        try {
            javaSandboxContainer.startContainer();
        } catch (Exception e) {
            log.error("Error starting sandbox container", e);

            ExecutionResultDto executionResultDto = new ExecutionResultDto();
            executionResultDto.setCompiled(false);
            executionResultDto.setExecution(false);
            executionResultDto.setMessage("Error starting sandbox container: " + e.getMessage());
            return executionResultDto;
        }

        // Go to the next filter
        if (next != null) {
            return next.apply(code, resultExpected);
        }

        return new ExecutionResultDto();
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}