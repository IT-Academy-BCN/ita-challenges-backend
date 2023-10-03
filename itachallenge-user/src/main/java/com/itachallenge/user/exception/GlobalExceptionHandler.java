package com.itachallenge.user.exception;

import com.itachallenge.user.dtos.ErrorResponseDto;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserScoreNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> notFoundResponse(UserScoreNotFoundException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponseDto(ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolation(ConstraintViolationException ex) {
        String errorMessage = ex.getMessage();
        return ResponseEntity.badRequest().body(new ErrorResponseDto(errorMessage));
    }

}