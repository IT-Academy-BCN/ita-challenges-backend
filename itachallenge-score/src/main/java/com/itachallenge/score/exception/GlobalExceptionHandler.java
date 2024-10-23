package com.itachallenge.score.exception;


import com.itachallenge.score.dto.MessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {


    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(DockerExecutionException.class)
    public ResponseEntity<MessageDTO> handleDockerExecutionException(DockerExecutionException ex) {
        log.error("Docker execution exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageDTO(ex.getMessage()));
    }

    @ExceptionHandler(ExecutionTimedOutException.class)
    public ResponseEntity<MessageDTO> handleExecutionTimedOutException(ExecutionTimedOutException ex) {
        log.error("Execution timed out exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageDTO(ex.getMessage()));
    }

    @ExceptionHandler(CodeExecutionException.class)
    public ResponseEntity<MessageDTO> handleCodeExecutionException(CodeExecutionException ex) {
        log.error("Code execution exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageDTO(ex.getMessage()));
    }
}
