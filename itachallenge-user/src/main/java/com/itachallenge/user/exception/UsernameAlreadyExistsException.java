package com.itachallenge.user.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("The username '" + username + "' is already registered.");
    }
}
