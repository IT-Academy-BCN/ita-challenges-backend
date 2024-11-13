package com.itachallenge.score.sandbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutoCloseableProcessTest {

    private Process process;
    private AutoCloseableProcess autoCloseableProcess;

    @BeforeEach
    void setUp() {
        process = mock(Process.class);
        autoCloseableProcess = new AutoCloseableProcess(process);
    }

    @Test
    void testClose() throws IOException, InterruptedException {
        autoCloseableProcess.close();

        verify(process).destroy();
        verify(process).waitFor();
    }

    @Test
    void testCloseWithInterruptedException() throws IOException, InterruptedException {
        doThrow(new InterruptedException()).when(process).waitFor();

        autoCloseableProcess.close();

        verify(process).destroy();
        verify(process).waitFor();
        assertTrue(Thread.currentThread().isInterrupted());
    }

    @Test
    void testGetProcess() {
        assertEquals(process, autoCloseableProcess.getProcess());
    }
}