package com.itachallenge.challenge.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ErrorResponseMessage.class)
class ErrorResponseMessageTest {
    //variables
    private final int STATUS_CODE = 500;
    private final String ERROR_MESSAGE = "INTERNAL SERVER ERROR";

    @MockBean
    private ErrorResponseMessage errorResponseMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testErrorResponseMessage() {
        ErrorResponseMessage response = new ErrorResponseMessage(STATUS_CODE, ERROR_MESSAGE);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode());
        assertEquals("INTERNAL SERVER ERROR", response.getMessage());
    }

}
