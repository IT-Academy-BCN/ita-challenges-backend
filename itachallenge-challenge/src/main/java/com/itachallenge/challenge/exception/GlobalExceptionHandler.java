package com.itachallenge.challenge.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseMessage> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus statusCode = (HttpStatus) ex.getStatusCode();
        ErrorResponseMessage errorResponseMessage = new ErrorResponseMessage(statusCode.value(), ex.getReason());
        return ResponseEntity.status(statusCode).body(errorResponseMessage);
    }

    @ExceptionHandler(BadUUIDException.class)
    public ResponseEntity<ErrorMessage> handleBadUUID(BadUUIDException ex) {
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(ChallengeNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleChallengeNotFoundException(ChallengeNotFoundException ex) {
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(new ErrorMessage("Parameter not valid", errors));
    }

    @AllArgsConstructor
    @Getter
    static
    class ErrorMessage {
        String msg;
        Map<String, String> errors = new HashMap<>();

        public ErrorMessage(String msg) {
            this.msg = msg;
        }
    }

}




