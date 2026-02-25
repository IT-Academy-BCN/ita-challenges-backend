package com.itachallenge.common.exception;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.itachallenge.challenge.dto.MessageDto;
import com.itachallenge.challenge.exception.*;
import com.itachallenge.common.exception.dto.ErrorResponseDto;
import com.itachallenge.common.exception.enums.ErrorCode;
import com.itachallenge.gamification.exception.ServiceUnavailableException;
import com.itachallenge.submission.exception.SubmissionNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import com.itachallenge.submission.exception.UnmodifiableSubmissionException;

import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
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
    public ResponseEntity<?> handleChallengeNotFoundException(ChallengeNotFoundException ex,
                                                              HttpServletRequest request) {
        if (request.getMethod().equals(HttpMethod.GET.name())
                && request.getRequestURI()
                .startsWith("/itachallenge/api/v1/challenge/challenges/")
                && !request.getRequestURI().endsWith("/byFilter")
                && !request.getRequestURI().endsWith("/related")) {
            ErrorResponseDto error = ErrorResponseDto.builder()
                    .errorCode(ErrorCode.CHALLENGE_NOT_FOUND.name())
                    .message(ex.getMessage())
                    .timestamp(Instant.now().toString())
                    .path(request.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
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

    @ExceptionHandler(SubmissionNotFoundException.class)
    public ResponseEntity<MessageDto> handleSubmissionNotFoundException(SubmissionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDto(ex.getMessage()));
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
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                   HttpServletRequest request) {
        if (request.getMethod().equals(HttpMethod.POST.name())
                && request.getRequestURI().equals("/itachallenge/api/v1/challenge/challenges")) {
            Map<String, Object> details = ex.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage,
                            (first, second) -> first
                    ));
            ErrorResponseDto error = ErrorResponseDto.builder()
                    .errorCode(ErrorCode.VALIDATION_ERROR.name())
                    .message("Validation failed")
                    .timestamp(Instant.now().toString())
                    .path(request.getRequestURI())
                    .details(details)
                    .build();
            return ResponseEntity.badRequest().body(error);
        }
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
    public ResponseEntity<?> handleInvalidFormat(InvalidFormatException ex,
                                                 HttpServletRequest request) {
        if (request.getMethod().equals(HttpMethod.POST.name())
                && request.getRequestURI().equals("/itachallenge/api/v1/challenge/challenges")) {
            Map<String, Object> details = Map.of(
                    "invalidValue", String.valueOf(ex.getValue())
            );
            ErrorResponseDto error = ErrorResponseDto.builder()
                    .errorCode(ErrorCode.VALIDATION_ERROR.name())
                    .message("Validation failed")
                    .timestamp(Instant.now().toString())
                    .path(request.getRequestURI())
                    .details(details)
                    .build();
            return ResponseEntity.badRequest().body(error);
        }
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

    @ExceptionHandler(UnmodifiableSubmissionException.class)
    public ResponseEntity<MessageDto> handleUnmodifiableSubmission(UnmodifiableSubmissionException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageDto(ex.getMessage()));
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<MessageDto> handleServiceUnavailableException(ServiceUnavailableException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new MessageDto(ex.getMessage()));
    }


}
