package com.itachallenge.challenge.exception;

import com.itachallenge.challenge.dto.ErrorResponseMessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ErrorResponseMessageDto.class)
class ErrorResponseMessageTest {
    //VARIABLES
    private final String ERROR_MESSAGE = "INTERNAL SERVER ERROR";
    private final HttpStatus INTERNAL_SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR;

    @MockBean
    private ErrorResponseMessageDto errorResponseMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testErrorResponseMessage() {
        ErrorResponseMessageDto response = new ErrorResponseMessageDto(ERROR_MESSAGE);

        StepVerifier.create(Mono.just(response))
                .expectNextMatches(resp -> {
                    assertEquals("INTERNAL SERVER ERROR", response.getMessage());
                    return true;
                })
                .verifyComplete();
    }

}
