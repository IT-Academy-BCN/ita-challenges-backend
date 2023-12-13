package com.itachallenge.score.service;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.List;

public class CompilationResult {
    private final boolean success;
    private final List<Diagnostic<? extends JavaFileObject>> diagnostics;

    public CompilationResult(boolean success, List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        this.success = success;
        this.diagnostics = diagnostics;
    }

    public CompilationResult(boolean success, Diagnostic<? extends JavaFileObject> diagnostic) {
        this.success = success;
        this.diagnostics = List.of(diagnostic);
    }

    public boolean hasErrors() {
        return !success;
    }

    public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {
        return diagnostics;
    }
}
