package com.itachallenge.score.sandbox;

import java.io.IOException;

public class AutoCloseableProcess implements AutoCloseable {
    private final Process process;

    public AutoCloseableProcess(Process process) {
        this.process = process;
    }

    public Process getProcess() {
        return process;
    }

    @Override
    public void close() throws IOException {
        process.destroy();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}