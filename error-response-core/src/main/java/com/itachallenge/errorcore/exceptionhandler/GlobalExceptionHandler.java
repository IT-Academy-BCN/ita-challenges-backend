package com.itachallenge.errorcore.exceptionhandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.itachallenge.errorcore.builder.ErrorResponseBuilder;
import com.itachallenge.errorcore.dto.APIErrorResponse;
import com.itachallenge.errorcore.exception.BaseApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

@Slf4j
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
public final class GlobalExceptionHandler {

    private final ErrorResponseBuilder responseBuilder;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIErrorResponse> handleAny(Exception e, HttpServletRequest request) {
        log.error("Unexpected error happened: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseBuilder
                        .buildError(e, request)
                );
    }

    @ExceptionHandler(BaseApiException.class)
    public ResponseEntity<APIErrorResponse> handleApiCustomException(BaseApiException e, HttpServletRequest request){
        String exceptionName = e.getClass().getSimpleName();
        log.error("Custom exception happened [{}]: {}", exceptionName, e.getMessage());
        return ResponseEntity
                .status(e.getInfo().status())
                .body(responseBuilder
                        .buildCustomExceptionError(e, request)
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIErrorResponse> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        log.error("Illegal argument happened: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseBuilder
                        .buildError(e, request)
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

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<APIErrorResponse> handleInvalidFormat(
            InvalidFormatException ex, HttpServletRequest request) {
        log.debug("Invalid format excetpion happened: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(responseBuilder.buildError(ex,request));
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            ServerWebInputException.class,
            DecodingException.class
    })
    public ResponseEntity<APIErrorResponse> handleWebFluxBindingErrors(
            Exception ex, HttpServletRequest request) {

        Throwable root = ex.getCause();
        while (root != null) {
            if (root instanceof InvalidFormatException invalid) {
                return handleInvalidFormat(invalid, request);
            }
            root = root.getCause();
        }

        log.debug("Unhandled binding exception type: {}", ex.getClass().getSimpleName());
        return ResponseEntity.badRequest()
                .body(responseBuilder.buildError(ex,request));
    }
}
