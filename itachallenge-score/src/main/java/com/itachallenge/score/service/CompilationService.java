package com.itachallenge.score.service;
import javax.tools.*;
import java.io.IOException;
import java.util.Arrays;

public class CompilationService {

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

}

