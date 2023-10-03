package com.itachallenge.user.exception;

import com.itachallenge.user.dtos.ErrorResponseDto;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @MockBean
    private UserScoreNotFoundException userScoreNotFoundException;

    @MockBean
    private ConstraintViolationException constraintViolationException;


    @Test
    void testStatusCodeAndMessageForException () {

        HttpStatus httpExpected = HttpStatus.BAD_REQUEST;
        String expected = "No challenges for user with id";
        when(userScoreNotFoundException.getMessage()).thenReturn("No challenges for user with id");

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.notFoundResponse(userScoreNotFoundException);

        StepVerifier.create(Mono.just(response))
                .expectNextMatches(resp -> {
                    assertEquals(expected, response.getBody().getMessage());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testHandleConstraintViolation() {
        final String ERROR_MESSAGE_EXPECTED = "The MongoDB UUID is invalid";

        when(constraintViolationException.getMessage()).thenReturn(ERROR_MESSAGE_EXPECTED);

        ResponseEntity<ErrorResponseDto> response = globalExceptionHandler.handleConstraintViolation(constraintViolationException);

        StepVerifier.create(Mono.just(response))
                .expectNextMatches(responseEntity -> {
                    ErrorResponseDto responseBody = responseEntity.getBody();

                    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
                    assertEquals(ERROR_MESSAGE_EXPECTED, responseBody.getMessage());
                    return true;

                })
                .verifyComplete();
    }
}
