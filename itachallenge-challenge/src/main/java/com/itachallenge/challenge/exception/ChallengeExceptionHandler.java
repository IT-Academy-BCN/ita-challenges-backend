package com.itachallenge.challenge.exception;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.itachallenge.errorcore.builder.ErrorResponseBuilder;
import com.itachallenge.errorcore.dto.APIErrorResponse;
import com.itachallenge.errorcore.exceptionhandler.BaseExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@RestControllerAdvice
public class ChallengeExceptionHandler extends BaseExceptionHandler {

    public ChallengeExceptionHandler(ErrorResponseBuilder responseBuilder) {
        super(responseBuilder);
    }

    @ExceptionHandler(ChallengeNotFoundException.class)
    public ResponseEntity<APIErrorResponse> handleChallengeNotFoundException(ChallengeNotFoundException ex, HttpServletRequest request) {
        log.error("ChallengeNotFound Exception happened:{}",ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBuilder.buildNotFoundError(ex,request));
    }

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<APIErrorResponse> handleTagNotFoundException(TagNotFoundException ex,HttpServletRequest request) {
        log.error("TagNotFound Exception happened: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBuilder.buildNotFoundError(ex,request));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        log.error("ResourceNotFound Exception happened: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBuilder.buildNotFoundError(ex,request));
    }

    @ExceptionHandler(LanguageNotFoundException.class)
    public ResponseEntity<APIErrorResponse> handleLanguageNotFoundException(LanguageNotFoundException ex, HttpServletRequest request) {
        log.error("LanguageNotFound Exception happened: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBuilder.buildNotFoundError(ex,request));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<APIErrorResponse> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        log.error("NotFound Exception happened: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBuilder.buildNotFoundError(ex,request));
    }

    @ExceptionHandler(BadUUIDException.class)
    public ResponseEntity<APIErrorResponse> handleBadUUIDException(BadUUIDException ex, HttpServletRequest request) {
        log.error("BadUUID Exception happened: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(responseBuilder.buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<APIErrorResponse> handleCustomBadRequestException(BadRequestException ex,HttpServletRequest request) {
        log.error("BadRequest Exception happened: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(responseBuilder.buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<APIErrorResponse> handleCustomInternalServerErrorException(InternalServerErrorException ex,HttpServletRequest request) {
        log.error("InternalServerError Exception happened: {}", ex.getMessage());
        return ResponseEntity.internalServerError()
                .body(responseBuilder.buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request));
    }

    @Override
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<APIErrorResponse> handleInvalidFormat(InvalidFormatException ex, HttpServletRequest request) {
        log.error("InvalidFormat Exception happened: {}", ex.getMessage());
        return buildTagUuidError(ex, request)
                .orElseGet(() ->
                        ResponseEntity.badRequest()
                                .body(responseBuilder.buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request))
                );
    }

    private Optional<ResponseEntity<APIErrorResponse>> buildTagUuidError(InvalidFormatException ex,HttpServletRequest request) {
        if (UUID.class.equals(ex.getTargetType())) {
            String badValue = ex.getValue().toString();
            boolean fromTags = ex.getPath().stream()
                    .map(Reference::getFieldName)
                    .anyMatch("tags"::equals);
            if (fromTags) {
                APIErrorResponse body = responseBuilder.buildError(HttpStatus.BAD_REQUEST,"invalid format UUID tag: " + badValue,request);
                return Optional.of(ResponseEntity
                        .badRequest()
                        .body(body));
            }
        }
        return Optional.empty();
    }
}