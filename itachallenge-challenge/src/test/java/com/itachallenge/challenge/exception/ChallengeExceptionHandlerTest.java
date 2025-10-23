package com.itachallenge.challenge.exception;

import com.itachallenge.errorcore.builder.ErrorResponseBuilder;
import com.itachallenge.errorcore.dto.APIErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ChallengeExceptionHandlerTest {

    @Mock
    private ErrorResponseBuilder responseBuilder;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ChallengeExceptionHandler handler;

    private APIErrorResponse fakeResponse(int status, String message) {
        return APIErrorResponse.builder()
                .status(status)
                .error(HttpStatus.valueOf(status).getReasonPhrase())
                .message(message)
                .path("/fake-path")
                .errors(List.of())
                .build();
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleConstraintViolation_ShouldReturn400() {
        ConstraintViolationException ex = new ConstraintViolationException("Validation failed", null);

        when(responseBuilder.buildConstraintViolationErrorResponse(eq(ex), any()))
                .thenReturn(fakeResponse(400, "Validation failed"));

        ResponseEntity<APIErrorResponse> response = handler.handleValidationExceptions(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Validation failed");
    }

    @Test
    void handleMethodArgumentNotValid_ShouldReturn400() {
        MethodArgumentNotValidException ex = org.mockito.Mockito.mock(MethodArgumentNotValidException.class);

        when(responseBuilder.buildArgumentNotValidErrorResponse(eq(ex), any()))
                .thenReturn(fakeResponse(400, "Argument not valid"));

        ResponseEntity<APIErrorResponse> response = handler.handleMethodArgumentNotValidException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Argument not valid");
    }

    @Test
    void handleResponseStatusException_ShouldReturnStatusFromBuilder() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");

        when(responseBuilder.buildStatusErrorResponse(eq(ex), any()))
                .thenReturn(fakeResponse(404, "Not found"));

        ResponseEntity<APIErrorResponse> response = handler.handleResponseStatusException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("Not found");
    }

    @Test
    void handleInternalServerError_ShouldReturn500() {
        InternalServerErrorException ex = new InternalServerErrorException("Internal error");

        when(responseBuilder.buildError(eq(HttpStatus.INTERNAL_SERVER_ERROR), eq("Internal error"), any()))
                .thenReturn(fakeResponse(500, "Internal error"));

        ResponseEntity<APIErrorResponse> response = handler.handleCustomInternalServerErrorException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).contains("Internal error");
    }

    @Test
    void handleChallengeNotFound_ShouldReturn404() {
        ChallengeNotFoundException ex = new ChallengeNotFoundException("Challenge not found");

        when(responseBuilder.buildNotFoundError(eq(ex), any()))
                .thenReturn(fakeResponse(404, "Challenge not found"));

        ResponseEntity<APIErrorResponse> response = handler.handleChallengeNotFoundException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("Challenge not found");
    }

    @Test
    void handleTagNotFound_ShouldReturn404() {
        TagNotFoundException ex = new TagNotFoundException("Tag not found");

        when(responseBuilder.buildNotFoundError(eq(ex), any()))
                .thenReturn(fakeResponse(404, "Tag not found"));

        ResponseEntity<APIErrorResponse> response = handler.handleTagNotFoundException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("Tag not found");
    }

    @Test
    void handleLanguageNotFound_ShouldReturn404() {
        LanguageNotFoundException ex = new LanguageNotFoundException("Language not found");

        when(responseBuilder.buildNotFoundError(eq(ex), any()))
                .thenReturn(fakeResponse(404, "Language not found"));

        ResponseEntity<APIErrorResponse> response = handler.handleLanguageNotFoundException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("Language not found");
    }

    @Test
    void handleResourceNotFound_ShouldReturn404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");

        when(responseBuilder.buildNotFoundError(eq(ex), any()))
                .thenReturn(fakeResponse(404, "Resource not found"));

        ResponseEntity<APIErrorResponse> response = handler.handleResourceNotFoundException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("Resource not found");
    }

    @Test
    void handleBadUUID_ShouldReturn400() {
        BadUUIDException ex = new BadUUIDException("Invalid UUID");

        when(responseBuilder.buildError(eq(HttpStatus.BAD_REQUEST), eq("Invalid UUID"), any()))
                .thenReturn(fakeResponse(400, "Invalid UUID"));

        ResponseEntity<APIErrorResponse> response = handler.handleBadUUIDException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).contains("Invalid UUID");
    }
}
