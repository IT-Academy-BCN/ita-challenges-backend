package com.itachallenge.score.service;

import com.itachallenge.score.service.*;
import org.junit.jupiter.api.Test;
import javax.tools.Diagnostic;
import javax.tools.JavaCompiler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CompilationServiceTest {

    @Test
    public void testSuccessfulExecution() {
        CompilationService compilationService = new CompilationService();
        String sourceCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";

        ExecutionResult executionResult = compilationService.compileAndRunCode(sourceCode);

        assertFalse(executionResult.hasErrors(), "Execution should succeed");
        assertEquals("Hello, World!\n", executionResult.getOutput().asText(), "Output should match expected");
    }

    @Test
    public void testCompilationWithErrors() {
        CompilationService compilationService = new CompilationService();
        String sourceCodeWithErrors = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\") }";

        ExecutionResult compilationResult = compilationService.compileAndRunCode(sourceCodeWithErrors);

        assertTrue(compilationResult.hasErrors(), "Compilation should fail");
        //assertFalse(compilationResult.getDiagnostics().isEmpty(), "There should be diagnostics");
    }
}
