package com.itachallenge.user.exception;

public class UserScoreNotFoundException extends RuntimeException{

    public UserScoreNotFoundException(int httpStatus, String message) {
        super(message);
    }
}
