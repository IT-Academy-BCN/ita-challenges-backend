package com.itachallenge.user.exception;

import com.itachallenge.githubcore.exception.GithubUnavailableException;
import com.itachallenge.user.dto.APIErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class UserGlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAny(Exception e) {
        log.error("Unexpected error happened: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error happened.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationExceptions(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameter format.");
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
//
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, ServerWebExchange exchange){
        log.error("Invalid request: {}", e.getMessage());

        APIErrorResponse response = APIErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(e.getMessage())
                .path(exchange.getRequest().getPath().value())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UnmodificableSolutionException.class)
    public ResponseEntity<APIErrorResponse> handleUnmodifiableSolutionException(UnmodificableSolutionException e,  ServerWebExchange exchange) {
        log.error("Resource already exists: {}", e.getMessage());

        APIErrorResponse response = APIErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(e.getMessage())
                .path(exchange.getRequest().getPath().value())
                .build();


        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<APIErrorResponse> handleInternalServerErrorException(InternalServerErrorException e, ServerWebExchange exchange) {
        log.error("Unexpected error happened: {}", e.getMessage());

        APIErrorResponse response = APIErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(e.getMessage())
                .path(exchange.getRequest().getPath().value())
                .build();


        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<APIErrorResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e, ServerWebExchange exchange) {
        log.error("The username already exists: {}", e.getMessage());

        APIErrorResponse response = APIErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CONFLICT.value())
                .error("The username already exists")
                .message("The username already exists")
                .path(exchange.getRequest().getPath().value())
                .build();


        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
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