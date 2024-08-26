package com.itachallenge.score.filter;

import com.itachallenge.score.docker.JavaSandboxContainer;
import lombok.Getter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.containers.GenericContainer;

import static org.slf4j.LoggerFactory.getLogger;

public class JavaContainerFilter implements Filter {

    private static final Logger log = getLogger(JavaContainerFilter.class.getName());

    private Filter next;

    @Autowired
    private JavaSandboxContainer javaSandboxContainer;

    @Getter
    private GenericContainer<?> sandboxContainer;



    @Override
    public boolean apply(String code) {

        try {
            javaSandboxContainer.startContainer();
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
