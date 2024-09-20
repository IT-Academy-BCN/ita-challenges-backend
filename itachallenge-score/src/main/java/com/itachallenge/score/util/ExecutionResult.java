package com.itachallenge.score.util;

public class ExecutionResult {

    private boolean compiled;
    private boolean execution;
    private boolean resultCodeMatch;
    private boolean passedAllFilters;
    private String message;

    public ExecutionResult() {}

    public ExecutionResult(boolean compiled, String message) {
        this.compiled = compiled;
        this.message = message;
    }

    public boolean isCompiled() {
        return compiled;
    }

    public void setCompiled(boolean compiled) {
        this.compiled = compiled;
    }

    public boolean isExecution() {
        return execution;
    }

    public void setExecution(boolean execution) {
        this.execution = execution;
    }

    public boolean isResultCodeMatch() {
        return resultCodeMatch;
    }

    public void setResultCodeMatch(boolean resultCodeMatch) {
        this.resultCodeMatch = resultCodeMatch;
    }

    public boolean isPassedAllFilters() {
        return passedAllFilters;
    }

    public void setPassedAllFilters(boolean passedAllFilters) {
        this.passedAllFilters = passedAllFilters;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}