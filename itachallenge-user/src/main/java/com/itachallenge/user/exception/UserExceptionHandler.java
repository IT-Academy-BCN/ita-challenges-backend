package com.itachallenge.user.exception;

import com.itachallenge.errorcore.builder.ErrorResponseBuilder;
import com.itachallenge.errorcore.dto.APIErrorResponse;
import com.itachallenge.errorcore.exceptionhandler.BaseExceptionHandler;
import com.itachallenge.githubcore.exception.GithubUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class UserExceptionHandler extends BaseExceptionHandler {

    public UserExceptionHandler(ErrorResponseBuilder responseBuilder) {
        super(responseBuilder);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(BadUUIDException.class)
    public ResponseEntity<String> handleBadUUIDException(BadUUIDException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided IDs are not valid.");
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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

    @ExceptionHandler(GithubUnavailableException.class)
    public ResponseEntity<APIErrorResponse> handleGithubUnavailable(GithubUnavailableException ex) {
        HttpStatus status;
        String securedMessage;

        log.error("GithubUnavailableException occurred: {}", ex.getMessage(), ex);

        if (ex.getCause() instanceof java.net.SocketTimeoutException) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            securedMessage = "The external GitHub service timed out.";
        } else if (ex.getCause() instanceof java.net.ConnectException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            securedMessage = "The external GitHub service is currently unavailable.";
        } else {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            securedMessage = "An external service error occurred.";
        }

        return ResponseEntity.status(status).body(
                APIErrorResponse.builder().message(securedMessage).error("External Service Error").build()
        );
    }

}