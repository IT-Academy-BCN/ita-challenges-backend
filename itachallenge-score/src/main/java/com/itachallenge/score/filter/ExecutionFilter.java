package com.itachallenge.score.filter;

import com.itachallenge.score.docker.DockerContainerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;

public class ExecutionFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(ExecutionFilter.class);
    private Filter next;


    @Override
    public boolean apply(String code) {

        GenericContainer<?> sandbox = ((JavaContainerFilter) next).getSandboxContainer();

        try {
            DockerContainerHelper.executeCommand(sandbox, "java", "-cp", "/home/sandbox", "CodeToExecute");
            log.info("Code executed successfully.");
        } catch (IOException | InterruptedException e) {
            log.error("Execution failed", e);
            return false;
        }

        return next == null || next.apply(code);
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}
