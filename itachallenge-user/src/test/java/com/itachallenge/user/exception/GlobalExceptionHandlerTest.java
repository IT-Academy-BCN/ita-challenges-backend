package com.itachallenge.user.exception;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
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

        ResponseEntity<GlobalExceptionHandler.ErrorMessage> response = globalExceptionHandler.notFoundResponse(userScoreNotFoundException);

        StepVerifier.create(Mono.just(response))
                .expectNextMatches(resp -> {
                    assertEquals(httpExpected, response.getStatusCode());
                    assertEquals(expected, response.getBody().msg);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testHandleConstraintViolation() {
        String errorMessageExpected = "The MongoDB UUID is invalid";

        when(constraintViolationException.getMessage()).thenReturn(errorMessageExpected);

        ResponseEntity<GlobalExceptionHandler.ErrorMessage> response = globalExceptionHandler.handleConstraintViolation(constraintViolationException);

        StepVerifier.create(Mono.just(response))
                .expectNextMatches(responseEntity -> {
                    HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
                    GlobalExceptionHandler.ErrorMessage responseBody = responseEntity.getBody();

                    return statusCode.equals(HttpStatus.BAD_REQUEST) && errorMessageExpected.equals(responseBody.msg);
                })
                .verifyComplete();
    }
}
