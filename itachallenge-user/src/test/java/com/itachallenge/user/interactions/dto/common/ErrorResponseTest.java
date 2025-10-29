package com.itachallenge.user.interactions.dto.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {
    @Test
    void shouldInstantiateErrorResponse() {
        ErrorResponse response = new ErrorResponse();
        assertNotNull(response);
    }
}