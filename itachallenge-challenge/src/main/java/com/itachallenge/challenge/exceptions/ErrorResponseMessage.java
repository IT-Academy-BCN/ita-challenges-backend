package com.itachallenge.challenge.exceptions;

import lombok.Getter;

@Getter
public class ErrorResponseMessage {
    private int statusCode;
    private String message;

    public ErrorResponseMessage(int statusCode, String message) {
        this.message = message;
        this.statusCode = statusCode;
    }

}