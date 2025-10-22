package com.itchallenge.errorcore.exceptionhandler;

import com.itchallenge.errorcore.builder.ErrorResponseBuilder;
import com.itchallenge.errorcore.dto.APIErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public abstract class BaseExceptionHandler {

    protected final ErrorResponseBuilder responseBuilder;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIErrorResponse> handleAny(Exception e, HttpServletRequest request) {
        log.error("Unexpected error happened: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseBuilder
                        .buildError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request)
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIErrorResponse> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        log.error("Illegal argument happened: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseBuilder
                        .buildError(HttpStatus.BAD_REQUEST, e.getMessage(), request)
                );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIErrorResponse> handleValidationExceptions(ConstraintViolationException ex, HttpServletRequest request) {
        log.error("Validation error happened: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseBuilder
                        .buildConstraintViolationErrorResponse(ex,request)
                );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.error("Type mismatch error happened: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseBuilder
                        .buildTypeMismatchErrorResponse(ex, request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        log.error("Method argument not valid error happened: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseBuilder
                        .buildArgumentNotValidErrorResponse(ex, request));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<APIErrorResponse> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        log.error("Status error happened: {}", ex.getMessage());
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(responseBuilder
                        .buildStatusErrorResponse(ex, request));
    }
}
