package com.itachallenge.user.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserScoreNotFoundException.class)
    public ResponseEntity<ErrorMessage> notFoundResponse(UserScoreNotFoundException ex) {
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessage> handleConstraintViolation(ConstraintViolationException ex) {
        String errorMessage = ex.getMessage();
        return ResponseEntity.badRequest().body(new ErrorMessage(errorMessage));
    }

    @AllArgsConstructor
    @Getter
    static
    class ErrorMessage {
        String msg;
    }
}