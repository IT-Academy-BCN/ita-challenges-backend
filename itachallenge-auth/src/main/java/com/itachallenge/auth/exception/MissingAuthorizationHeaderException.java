package com.itachallenge.auth.exception;

public class MissingAuthorizationHeaderException extends RuntimeException {
    public MissingAuthorizationHeaderException(String message) {

        super(message);
    }
}
