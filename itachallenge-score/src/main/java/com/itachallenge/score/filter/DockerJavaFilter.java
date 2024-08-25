package com.itachallenge.score.filter;

import com.itachallenge.score.docker.DockerContainerHelper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

public class DockerJavaFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(DockerJavaFilter.class);

    private Filter next;

    @Getter
    private GenericContainer<?> sandboxContainer;

    @Override
    public boolean apply(String code) {
        try {
            log.info("Attempting to create sandbox container...");
            sandboxContainer = DockerContainerHelper.createJavaSandboxContainer();
            log.info("Sandbox container started successfully");
        } catch (Exception e) {
            log.error("Error starting sandbox container: {}", e.getMessage());
            return false;
        }

        boolean result = next == null || next.apply(code);
        return result;
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}