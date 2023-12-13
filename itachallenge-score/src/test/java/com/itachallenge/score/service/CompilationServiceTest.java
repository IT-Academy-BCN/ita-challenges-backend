package com.itachallenge.score.service;

import com.itachallenge.score.service.*;
import org.junit.jupiter.api.Test;
import javax.tools.Diagnostic;
import javax.tools.JavaCompiler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CompilationServiceTest {

    @Test
    public void testSuccessfulCompilation() {
        CompilationService compilationService = new CompilationService();
        String sourceCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";
        
        CompilationResult compilationResult = compilationService.compileCode(sourceCode);
        
        assertFalse(compilationResult.hasErrors(), "Compilation should succeed");
        assertTrue(compilationResult.getDiagnostics().isEmpty(), "There should be no diagnostics");
    }

    @Test
    public void testCompilationWithErrors() {
        CompilationService compilationService = new CompilationService();
        String sourceCodeWithErrors = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\") }";
        
        CompilationResult compilationResult = compilationService.compileCode(sourceCodeWithErrors);
        
        assertTrue(compilationResult.hasErrors(), "Compilation should fail");
        assertFalse(compilationResult.getDiagnostics().isEmpty(), "There should be diagnostics");
    }
/*
    @Test
    public void testCompilationWithIOException() {
        CompilationService compilationService = new CompilationService() {
            @Override
            protected JavaCompiler getCompiler() {
                // Simula una situación donde no se puede obtener el compilador (por ejemplo, en entornos sin JDK).
                return null;
            }
        };
        String sourceCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";
        
        CompilationResult compilationResult = compilationService.compileCode(sourceCode);
        
        assertTrue(compilationResult.hasErrors(), "Compilation should fail");
        assertEquals(1, compilationResult.getDiagnostics().size(), "There should be one diagnostic");
        assertEquals(Diagnostic.Kind.ERROR, compilationResult.getDiagnostics().get(0).getKind(), "The diagnostic should be an error");
        assertEquals("IOException during compilation", compilationResult.getDiagnostics().get(0).getMessage(null), "Incorrect error message");
    }*/
}
