package com.itachallenge.score.filter;

import com.itachallenge.score.docker.DockerContainerHelper;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

public class DockerJavaFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(DockerJavaFilter.class.getName());

    private final DockerContainerHelper dockerContainerHelper;
    private Filter next;

    @Getter
    private GenericContainer<?> sandboxContainer;

    public DockerJavaFilter(DockerContainerHelper dockerContainerHelper) {
        this.dockerContainerHelper = dockerContainerHelper;
    }

    @Override
    public boolean apply(String code) {
        try {
            sandboxContainer = dockerContainerHelper.createJavaSandboxContainer();
            log.info("Sandbox container started");
        } catch (Exception e) {
            log.error("Error starting sandbox container", e);
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