package com.itachallenge.challenge.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ErrorMessage.class)
class ErrorMessageTest {

    @MockBean
    private ErrorMessage errorMessage;

    @BeforeEach
    void setUp() {
      MockitoAnnotations.openMocks(this);
    }

    @Test
    void testErrorMessage() {
        Map<String, String> errors = Map.of("Field1", "DefaultMessage1", "Field2", "DefaultMessage2");
        String message = "Parameter not valid";
        ErrorMessage response = new ErrorMessage(message, errors);

        StepVerifier.create(Mono.just(response))
                .expectNextMatches(resp -> {
                    assertEquals("Parameter not valid", response.getMessage());
                    assertEquals(errors, response.getErrors());
                    return true;
                })
                .verifyComplete();
    }

}
