package com.itachallenge.challenge.exception;

import com.itachallenge.challenge.dto.MessageDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.validation.FieldError;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<MessageDto>> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus statusCode = (HttpStatus) ex.getStatusCode();
        String errorMessage;
        Object[] detailMessageArguments = ex.getDetailMessageArguments();
        if (detailMessageArguments == null || detailMessageArguments.length == 0) {
            errorMessage = "Validation failed";
        } else {
            errorMessage = Arrays.stream(detailMessageArguments)
                    .skip(1)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            errorMessage = errorMessage.replace("[", "").replace("]", "");
        }
        MessageDto errorResponseMessage = new MessageDto(errorMessage);
        return Mono.just(ResponseEntity.status(statusCode).body(errorResponseMessage));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<MessageDto>> handleConstraintViolation(ConstraintViolationException ex) {
        String constraintMessage = ex.getConstraintViolations()
                .stream().findFirst().map(ConstraintViolation::getMessage).orElse("Invalid value");
        return Mono.just(ResponseEntity.ok().body(new MessageDto(constraintMessage)));
    }

    @ExceptionHandler(ChallengeNotFoundException.class)
    public Mono<ResponseEntity<MessageDto>> handleChallengeNotFoundException(ChallengeNotFoundException ex) {
        MessageDto messageDto = new MessageDto("Challenge Id not found");
        return Mono.just(ResponseEntity.ok().body(messageDto));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<MessageDto>>  handleResourceNotFoundException(ResourceNotFoundException ex) {
        MessageDto messageDto = new MessageDto(ex.getMessage());
        return Mono.just(ResponseEntity.ok().body(messageDto));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<MessageDto>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        // Construir un mensaje de error que concatene todos los mensajes de error de validación.
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        if (errorMessage.isEmpty()) {
            // Si no hay mensajes de error específicos, usar un mensaje por defecto.
            errorMessage = "Validation failed due to input errors";
        }

        // Construir y devolver una respuesta con el mensaje de error en formato JSON.
        return Mono.just(ResponseEntity.badRequest().body(new MessageDto(errorMessage)));
    }


    @ExceptionHandler(BadUUIDException.class)
    public Mono<ResponseEntity<MessageDto>> handleBadUUIDException(BadUUIDException ex) {
        MessageDto messageDto = new MessageDto(ex.getMessage());
        return Mono.just(ResponseEntity.badRequest().body(messageDto));
    }
}