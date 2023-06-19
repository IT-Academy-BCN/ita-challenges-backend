package com.itachallenge.challenge.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionHandlingTest {

    @Test
    void testHandleBadUUID() {
        ExceptionHandling exceptionHandling = new ExceptionHandling();
        ResponseEntity<ExceptionHandling.ErrorMessage> responseEntity = exceptionHandling.handleBadUUID();

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("The provided UUID is not valid", responseEntity.getBody().getMsg());
    }

    @Test
    void testErrorMessage() {
        String expectedMsg = "The provided UUID is not valid";

        ExceptionHandling.ErrorMessage errorMessage = new ExceptionHandling.ErrorMessage(expectedMsg);

        assertEquals(expectedMsg, errorMessage.getMsg());
        assertEquals(errorMessage, new ExceptionHandling.ErrorMessage(expectedMsg));
        assertEquals(errorMessage.hashCode(), new ExceptionHandling.ErrorMessage(expectedMsg).hashCode());
    }
}
