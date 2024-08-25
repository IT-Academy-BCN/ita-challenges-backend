package com.itachallenge.score.filter;

import com.itachallenge.score.docker.DockerContainerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;

public class CompilationFilter implements Filter{

    private static final Logger log = LoggerFactory.getLogger(CompilationFilter.class);
    private Filter next;



    @Override
    public boolean apply(String code) {

        GenericContainer<?> sandbox = ((JavaContainerFilter) next).getSandboxContainer();

        try {
            DockerContainerHelper.copyFileToContainer(sandbox, code, "/home/sandbox/CodeToExecute.java");
            DockerContainerHelper.executeCommand(sandbox, "javac", "/home/sandbox/CodeToExecute.java");
            log.info("Code compiled successfully.");
        } catch (IOException | InterruptedException e) {
            log.error("Error compiling code");
            return false;
        }

        return next == null || next.apply(code);
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}
