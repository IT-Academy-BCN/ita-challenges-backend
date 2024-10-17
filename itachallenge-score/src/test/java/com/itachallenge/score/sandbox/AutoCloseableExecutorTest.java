package com.itachallenge.score.sandbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class AutoCloseableExecutorTest {

    private ExecutorService executorService;
    private AutoCloseableExecutor autoCloseableExecutor;

    @BeforeEach
    void setUp() {
        executorService = Executors.newSingleThreadExecutor();
        autoCloseableExecutor = new AutoCloseableExecutor(executorService);
    }

    @Test
    void testClose() throws InterruptedException {
        autoCloseableExecutor.close();

        assertTrue(executorService.isShutdown());
        assertTrue(executorService.awaitTermination(30, TimeUnit.SECONDS));
    }

    @Test
    void testGetExecutorService() {
        assertEquals(executorService, autoCloseableExecutor.getExecutorService());
    }
}