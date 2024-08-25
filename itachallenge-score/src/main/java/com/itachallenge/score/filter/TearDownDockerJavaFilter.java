package com.itachallenge.score.filter;

import org.testcontainers.containers.GenericContainer;

public class TearDownDockerJavaFilter implements Filter {

    private GenericContainer<?> sandboxContainer;

    public TearDownDockerJavaFilter() {
        // No-argument constructor
    }

    public void setSandboxContainer(GenericContainer<?> sandboxContainer) {
        this.sandboxContainer = sandboxContainer;
    }

    @Override
    public boolean apply(String sourceCode) {
        if (sandboxContainer != null) {
            sandboxContainer.stop();
        }
        return true;
    }

    @Override
    public void setNext(Filter next) {

    }
}