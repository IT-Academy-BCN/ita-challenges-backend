package com.itachallenge.score.service;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.Locale;

public class FakeDiagnostic implements Diagnostic<JavaFileObject> {

    private final String message;

    public FakeDiagnostic(String message) {
        this.message = message;
    }

    @Override
    public Kind getKind() {
        return Kind.ERROR;
    }    

    @Override
    public long getPosition() {
        return Diagnostic.NOPOS;
    }

    @Override
    public long getStartPosition() {
        return Diagnostic.NOPOS;
    }

    @Override
    public long getEndPosition() {
        return Diagnostic.NOPOS;
    }

    @Override
    public long getLineNumber() {
        return Diagnostic.NOPOS;
    }

    @Override
    public long getColumnNumber() {
        return Diagnostic.NOPOS;
    }

    @Override
    public JavaFileObject getSource() {
        return null;
    }

    @Override
    public String getMessage(Locale locale) {
        return message;
    }

    @Override
    public String getCode() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCode'");
    }
}
