package com.itachallenge.challenge.exception;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.itachallenge.challenge.dto.APIErrorResponse;
import com.itachallenge.challenge.dto.FieldErrorDto;
import com.itachallenge.challenge.dto.MessageDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;

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
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageDto(ex.getMessage()));
    }

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<MessageDto> handleTagNotFoundException(TagNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageDto(ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageDto> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageDto(ex.getMessage()));
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
    public ResponseEntity<APIErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Locale locale = LocaleContextHolder.getLocale();

        // Build list of field-level errors
        List<FieldErrorDto> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> FieldErrorDto.builder()
                        .objectName(error.getObjectName())
                        .field(error.getField())
                        .message(messageSource.getMessage(error, locale))
                        .build())
                .toList();
        
        // Build unified structured response
        String objectName = ex.getBindingResult().getObjectName();
        return ResponseEntity.badRequest()
                .body(buildValidationErrorResponse(objectName, fieldErrors, request));
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
        return buildTagUuidError(ex)
                .orElseGet(() ->
                        ResponseEntity.badRequest().body(new MessageDto(ex.getOriginalMessage()))
                );
    }

    private Optional<ResponseEntity<MessageDto>> buildTagUuidError(InvalidFormatException ex) {
        if (UUID.class.equals(ex.getTargetType())) {
            String badValue = ex.getValue().toString();
            boolean fromTags = ex.getPath().stream()
                    .map(Reference::getFieldName)
                    .anyMatch("tags"::equals);
            if (fromTags) {
                MessageDto body = new MessageDto("invalid format UUID tag: " + badValue);
                return Optional.of(ResponseEntity
                        .badRequest()
                        .body(body));
            }
        }
        return Optional.empty();
    }

    private APIErrorResponse buildValidationErrorResponse(
            String objectName,
            List<FieldErrorDto> fieldErrors,
            HttpServletRequest request) {

        String message = String.format(
                "Validation failed for one or more fields in %s.",
                objectName.replace("Dto", "").toLowerCase()
        );

        return APIErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .errors(fieldErrors)
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();
    }
}