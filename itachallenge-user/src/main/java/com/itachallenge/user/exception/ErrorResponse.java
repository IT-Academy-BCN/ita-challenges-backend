package com.itachallenge.user.exception;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorResponse {

    private int statusCode;
    private String message;

    public ErrorResponse(String message, HttpStatus httpStatus) {
        this.message = message;
        this.statusCode = statusCode;
    }


}
