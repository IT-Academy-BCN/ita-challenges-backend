package com.itachallenge.score.sandbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoCloseableExecutor implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(AutoCloseableExecutor.class);
    private final ExecutorService executorService;

    public AutoCloseableExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void close() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.error("Executor did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}