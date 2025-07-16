package com.itachallenge.jwtcore.exception;

public class InvalidRoleChangeRequestException extends RuntimeException {
    public InvalidRoleChangeRequestException(String message) {
        super(message);
    }
}