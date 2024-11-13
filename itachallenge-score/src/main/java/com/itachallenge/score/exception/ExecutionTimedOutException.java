package com.itachallenge.score.exception;

public class ExecutionTimedOutException extends RuntimeException {
    public ExecutionTimedOutException(String message) {
        super(message);
    }
}
