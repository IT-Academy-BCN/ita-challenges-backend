package com.itachallenge.user.exception;

import com.itachallenge.errorcore.builder.ErrorResponseBuilder;
import com.itachallenge.errorcore.dto.APIErrorResponse;
import com.itachallenge.errorcore.exceptionhandler.BaseExceptionHandler;
import com.itachallenge.githubcore.exception.GithubUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<APIErrorResponse> handleBadRequestException(BadRequestException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(responseBuilder.buildError(HttpStatus.BAD_REQUEST,e.getMessage(),request));
    }

    @ExceptionHandler(BadUUIDException.class)
    public ResponseEntity<APIErrorResponse> handleBadUUIDException(BadUUIDException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(responseBuilder.buildError(HttpStatus.BAD_REQUEST,"validation.uuid.invalid",request));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<APIErrorResponse> handleNotFoundException(NotFoundException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(responseBuilder.buildError(HttpStatus.NOT_FOUND,e.getMessage(),request));
    }

    @ExceptionHandler(UnmodificableSolutionException.class)
    public ResponseEntity<APIErrorResponse> handleUnmodifiableSolutionException(UnmodificableSolutionException e,HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(responseBuilder.buildError(HttpStatus.CONFLICT, e.getMessage(), request));
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<APIErrorResponse> handleInternalServerErrorException(InternalServerErrorException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseBuilder.buildError(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage(),request));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<APIErrorResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e,HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(responseBuilder.buildError(HttpStatus.CONFLICT,e.getMessage(), request));
    }

    @ExceptionHandler(GithubUnavailableException.class)
    public ResponseEntity<APIErrorResponse> handleGithubUnavailable(GithubUnavailableException ex, HttpServletRequest request) {
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
                responseBuilder.buildError(status,securedMessage,request));
    }
}