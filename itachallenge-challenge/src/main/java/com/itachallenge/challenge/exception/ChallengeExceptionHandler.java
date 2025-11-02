package com.itachallenge.challenge.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.itachallenge.errorcore.builder.ErrorResponseBuilder;
import com.itachallenge.errorcore.dto.APIErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ChallengeExceptionHandler {
    private final ErrorResponseBuilder responseBuilder;

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<APIErrorResponse> handleInvalidFormat(InvalidFormatException ex, HttpServletRequest request) {
        return buildTagUuidError(ex, request)
                .orElseGet(() ->
                        ResponseEntity.badRequest().body(responseBuilder.buildError(ex,request))
                );
    }

    private Optional<ResponseEntity<APIErrorResponse>> buildTagUuidError(InvalidFormatException ex, HttpServletRequest request) {
        if (UUID.class.equals(ex.getTargetType())) {
            String badValue = ex.getValue().toString();
            boolean fromTags = ex.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .anyMatch("tags"::equals);
            if (fromTags) {
                APIErrorResponse body =APIErrorResponse.builder()
                        .message("invalid format UUID tag: " + badValue)
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .path(request.getRequestURI()).build();
                return Optional.of(ResponseEntity
                        .badRequest()
                        .body(body));
            }
        }
        return Optional.empty();
    }

}
