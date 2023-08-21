package com.itachallenge.user.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    private int statusCode;
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }


}
