package com.itachallenge.challenge.exception;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.itachallenge.challenge.dto.MessageDto;
import com.itchallenge.errorcore.builder.ErrorResponseBuilder;
import com.itchallenge.errorcore.dto.APIErrorResponse;
import com.itchallenge.errorcore.exceptionhandler.BaseExceptionHandler;
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
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(responseBuilder
                        .buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request)
                );
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

    private APIErrorResponse buildNotFoundError(RuntimeException ex, HttpServletRequest request){
        return responseBuilder.buildError(HttpStatus.NOT_FOUND,ex.getMessage(),request);
    }
}