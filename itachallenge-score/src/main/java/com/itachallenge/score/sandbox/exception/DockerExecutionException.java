package com.itachallenge.score.sandbox.exception;

public class DockerExecutionException extends RuntimeException {
    public DockerExecutionException(String message, Throwable cause) {
        super(message);
    }
}
