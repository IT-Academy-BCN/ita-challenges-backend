package com.itachallenge.challenge.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler(BadUUIDException.class)
    public ResponseEntity<ErrorMessage> handleBadUUID(){
        return ResponseEntity.badRequest().body(new ErrorMessage("The provided UUID is not valid"));
    }

    @Data
    @AllArgsConstructor
    static
    class ErrorMessage{
        String msg;
    }
}
