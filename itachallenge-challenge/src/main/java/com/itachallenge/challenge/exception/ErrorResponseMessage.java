package com.itachallenge.challenge.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class ErrorResponseMessage {
    private int statusCode;
    private String message;

    public ErrorResponseMessage(int statusCode, String message) {
        this.message = message;
        this.statusCode = statusCode;
    }

}
