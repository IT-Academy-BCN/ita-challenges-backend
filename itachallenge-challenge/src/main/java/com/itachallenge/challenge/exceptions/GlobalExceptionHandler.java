package com.itachallenge.challenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseMessage> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus statusCode = ex.getStatus();
        ErrorResponseMessage errorResponseMessage = new ErrorResponseMessage(statusCode.value(), ex.getReason());
        return ResponseEntity.status(statusCode).body(errorResponseMessage);
    }

}
