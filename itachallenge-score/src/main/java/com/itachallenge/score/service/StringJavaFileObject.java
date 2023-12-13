package com.itachallenge.score.service;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

public class StringJavaFileObject extends SimpleJavaFileObject {

    private final String sourceCode;

    public StringJavaFileObject(String className, String sourceCode) {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.sourceCode = sourceCode;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return sourceCode;
    }
}
