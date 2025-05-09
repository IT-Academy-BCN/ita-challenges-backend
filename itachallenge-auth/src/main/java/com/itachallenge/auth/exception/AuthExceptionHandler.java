package com.itachallenge.auth.exception;


import com.itachallenge.challenge.dto.MessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AuthExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AuthExceptionHandler.class);

    @ExceptionHandler(ForbiddenAccessException.class)
    public ResponseEntity<MessageDto> handleForbiddenAccess(ForbiddenAccessException forbiddenAccessException){
        log.warn("Access denied: {}", forbiddenAccessException.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new MessageDto(forbiddenAccessException.getMessage()));
    }

    @ExceptionHandler(MissingAuthorizationHeaderException.class)
    public ResponseEntity<MessageDto> handleMissingAuthHeader(MissingAuthorizationHeaderException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MessageDto(ex.getMessage()));
    }
}