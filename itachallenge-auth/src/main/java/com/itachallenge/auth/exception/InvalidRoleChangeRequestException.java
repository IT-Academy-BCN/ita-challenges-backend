package com.itachallenge.auth.exception;

public class InvalidRoleChangeRequestException extends RuntimeException {
    public InvalidRoleChangeRequestException(String message) {
        super(message);
    }
}