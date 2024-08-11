package com.itachallenge.score.filter;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

public class TearDownDockerJavaFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(TearDownDockerJavaFilter.class.getName());

    @Setter
    private GenericContainer<?> sandboxContainer;
    private Filter next;

    @Override
    public boolean apply(String code) {
        boolean result = next == null || next.apply(code);

        if (sandboxContainer != null) {
            sandboxContainer.stop();
            log.info("Sandbox container stopped");
        }
        return result;
    }

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }

}
