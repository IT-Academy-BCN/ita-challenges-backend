package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;
import com.itachallenge.score.util.ObfuscationDetector;

public class ObfuscationFilter implements Filter {
    private Filter next;

    @Override
    public void setNext(Filter next) {
        this.next = next;
    }

    @Override
    public ExecutionResult apply(String sourceCode) {
        ExecutionResult executionResult = ObfuscationDetector.detectAndModify(sourceCode);

        if (next != null) {
            return next.apply(executionResult.getMessage());
        } else {
            return executionResult;
        }
    }
}
