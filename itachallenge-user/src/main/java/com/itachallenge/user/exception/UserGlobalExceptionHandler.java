package com.itachallenge.user.exception;

import com.itachallenge.githubcore.exception.GithubUnavailableException;
import com.itachallenge.user.dto.APIErrorResponse;
import com.itachallenge.user.dto.FieldErrorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ServerWebExchange;

import jakarta.validation.ConstraintViolationException;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class UserGlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIErrorResponse> handleAny(Exception e, ServerWebExchange exchange) {

        log.error("Unexpected error happened: {}", e.getMessage());

        APIErrorResponse response = APIErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("An unexpected error occurred.")
                .path(exchange.getRequest().getPath().value())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIErrorResponse> handleIllegalArgument(IllegalArgumentException e, ServerWebExchange exchange) {
        log.error("Illegal argument: {}", e.getMessage(), e);

        APIErrorResponse response = APIErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Invalid input provided. Please check your request.")
                .path(exchange.getRequest().getPath().value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIErrorResponse> handleValidationExceptions(ConstraintViolationException ex, ServerWebExchange exchange) {
        log.error("Validation error: {}",ex.getMessage());

        List<FieldErrorDto> fieldErrors = ex.getConstraintViolations()
                .stream()
                .map(cv -> FieldErrorDto.builder()
                        .objectName(cv.getRootBeanClass().getSimpleName())
                        .field(cv.getPropertyPath().toString())
                        .message(cv.getMessage())
                        .build())
                .toList();

        APIErrorResponse response = APIErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .path(exchange.getRequest().getPath().value())
                .errors(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, ServerWebExchange exchange) {

        log.error("MethodArgumentTypeMismatchException: parameter '{}' with value '{}' could not be converted to type '{}'",
                ex.getName(),
                ex.getValue(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown");

        String containingClassName = (ex.getParameter() != null && ex.getParameter().getContainingClass() != null)
                ? ex.getParameter().getContainingClass().getSimpleName()
                : "UnknownClass";

        FieldErrorDto fieldError = new FieldErrorDto(
                containingClassName,
                ex.getName(),
                String.format("Value '%s' could not be converted to %s",
                        ex.getValue(),
                        ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown")
        );

        APIErrorResponse response = APIErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("MethodArgumentTypeMismatchException")
                .path(exchange.getRequest().getPath().value())
                .errors(List.of(fieldError))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(BadUUIDException.class)
    public ResponseEntity<String> handleBadUUIDException(BadUUIDException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided IDs are not valid.");
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<String> handleDatabaseException(DatabaseException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error: " + e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(UnmodificableSolutionException.class)
    public ResponseEntity<String> handleUnmodifiableSolutionException(UnmodificableSolutionException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<String> handleInternalServerErrorException(InternalServerErrorException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

//    @ExceptionHandler(GithubUnavailableException.class)
//    public ResponseEntity<APIErrorResponse> handleGithubUnavailable(GithubUnavailableException ex) {
//        HttpStatus status;
//
//        if ("timeout".equalsIgnoreCase(ex.getMessage())) {
//            status = HttpStatus.GATEWAY_TIMEOUT; // 504
//        } else {
//            status = HttpStatus.SERVICE_UNAVAILABLE; // 503
//        }
//
//        return ResponseEntity.status(status).body(
//                new APIErrorResponse("GitHub API error", ex.getMessage(), Instant.now())
//        );
//    }

}