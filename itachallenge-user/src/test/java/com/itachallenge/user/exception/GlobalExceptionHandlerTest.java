package com.itachallenge.user.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
public class GlobalExceptionHandlerTest {

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @MockBean
    private UserScoreNotFoundException userScoreNotFoundException;

    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

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
}
