package com.itachallenge.score.service;
import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import org.codehaus.janino.SimpleCompiler;


public class CompilationService {

    public ExecutionResult compileAndRunCode(String sourceCode) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticCollector, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(new StringJavaFileObject("Main", sourceCode));

            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnosticCollector,
                    null,
                    null,
                    compilationUnits
            );

            boolean success = task.call();
            if (!success) {
                return new ExecutionResult(false, diagnosticCollector.getDiagnostics().toString());
            }

            SimpleCompiler runtimeCompiler = new SimpleCompiler();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream old = System.out;
            System.setOut(ps);

            try {
                runtimeCompiler.cook(sourceCode);
                Class<?> compiledClass = runtimeCompiler.getClassLoader().loadClass("Main");
                compiledClass.getMethod("main", String[].class).invoke(null, (Object) new String[]{});

                System.out.flush();
                System.setOut(old);

                return new ExecutionResult(true, baos.toString());
            } catch (Exception e) {
                e.printStackTrace();
                System.setOut(old);
                return new ExecutionResult(false, e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Diagnostic<? extends JavaFileObject> fakeDiagnostic = new FakeDiagnostic("IOException during compilation");
            return new ExecutionResult(false, Arrays.asList(fakeDiagnostic).toString());
        }
    }

    protected JavaCompiler getCompiler() {
        return ToolProvider.getSystemJavaCompiler();
    }


/*
    public CompilationResult compileCode(String sourceCode) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<>();

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticCollector, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(new StringJavaFileObject("Main", sourceCode));

            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    fileManager,
                    diagnosticCollector,
                    null,
                    null,
                    compilationUnits
            );

            boolean success = task.call();
            return new CompilationResult(success, diagnosticCollector.getDiagnostics());
        } catch (IOException e) {
            e.printStackTrace();
            // En caso de una excepción IOException durante la compilación, puedes retornar un diagnóstico ficticio.
            Diagnostic<? extends JavaFileObject> fakeDiagnostic = new FakeDiagnostic("IOException during compilation");
            return new CompilationResult(false, Arrays.asList(fakeDiagnostic));
        }
    }

    protected JavaCompiler getCompiler() {
        return ToolProvider.getSystemJavaCompiler();
    }
*/
}

