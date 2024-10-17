package com.itachallenge.score.exception;

public class DockerExecutionException extends RuntimeException {
    public DockerExecutionException(String message, Throwable cause) {
        super(message);
    }
}
