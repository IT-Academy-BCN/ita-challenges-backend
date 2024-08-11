package com.itachallenge.score.filter;

import com.itachallenge.score.docker.DockerContainerHelper;
import lombok.Getter;
import org.slf4j.Logger;
import org.testcontainers.containers.GenericContainer;

import static org.slf4j.LoggerFactory.getLogger;

public class DockerJavaFilter implements Filter {

    private static final Logger log = getLogger(DockerJavaFilter.class.getName());

    private Filter next;

    @Getter
    private GenericContainer<?> sandboxContainer;



    @Override
    public boolean apply(String code) {

        try {
            sandboxContainer = DockerContainerHelper.createJavaSandboxContainer();
            log.info("Sandbox container started");
        } catch (Exception e) {
            log.error("Error starting sandbox container");
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
