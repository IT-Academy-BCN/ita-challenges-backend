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
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.util.Arrays;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<MessageDto> handleResponseStatusException(ResponseStatusException ex) {
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
        return ResponseEntity.status(statusCode).body(errorResponseMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<MessageDto> handleConstraintViolation(ConstraintViolationException ex) {
        String constraintMessage = ex.getConstraintViolations()
                .stream().findFirst().map(ConstraintViolation::getMessage).orElse("Invalid value");
        return ResponseEntity.badRequest().body(new MessageDto(constraintMessage));
    }

    @ExceptionHandler(ChallengeNotFoundException.class)
    public ResponseEntity<MessageDto> handleChallengeNotFoundException(ChallengeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.OK).body(new MessageDto(ex.getMessage()));
    }

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<MessageDto> handleTagNotFoundException(TagNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageDto(ex.getMessage()));
    }

    @ExceptionHandler(ChallengeNotFoundReturn404Exception.class)
    public ResponseEntity<MessageDto> handleChallengeNotFoundReturn404Exception(ChallengeNotFoundReturn404Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageDto(ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageDto> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.ok().body(new MessageDto(ex.getMessage()));
    }

    @ExceptionHandler(LanguageNotFoundException.class)
    public ResponseEntity<MessageDto> handleLanguageNotFoundException(LanguageNotFoundException ex) {
        return ResponseEntity.badRequest().body(new MessageDto(ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<MessageDto> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.ok().body(new MessageDto(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(new MessageDto(ex.getMessage()));
    }

    @ExceptionHandler(BadUUIDException.class)
    public ResponseEntity<MessageDto> handleBadUUIDException(BadUUIDException ex) {
        return ResponseEntity.badRequest().body(new MessageDto(ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<MessageDto> handleCustomBadRequestException(BadRequestException ex) {
        return ResponseEntity.badRequest().body(new MessageDto(ex.getMessage()));
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<MessageDto> handleCustomInternalServerErrorException(InternalServerErrorException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageDto(ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<MessageDto> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDto(e.getMessage()));
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<MessageDto> handleInvalidFormat(InvalidFormatException ex) {
        return ResponseEntity.badRequest().body(new MessageDto(ex.getMessage()));
    }

}