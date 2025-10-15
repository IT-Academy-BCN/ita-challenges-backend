package com.itachallenge.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class APIErrorResponseTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldBuildAPIErrorResponse(){
        Instant now = Instant.now();
        FieldErrorDto fieldError = new FieldErrorDto("AdminCreateUserRequestDto","username", "must not be empty");

        APIErrorResponse response = APIErrorResponse.builder()
                .timestamp(now)
                .status(400)
                .error("Bad Request")
                .message("Validation failed")
                .path("api/v1/user")
                .errors(List.of(fieldError))
                .build();

        assertThat(response.getTimestamp()).isEqualTo(now);
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getError()).isEqualTo("Bad Request");
        assertThat(response.getMessage()).isEqualTo("Validation failed");
        assertThat(response.getPath()).isEqualTo("api/v1/user");
        assertThat(response.getErrors()).containsExactly(fieldError);
    }

}

//    @Test
//    void testNoArgsConstructorAndSetters() {
//        Instant now = Instant.now();
//        APIErrorResponse errorResponse = new APIErrorResponse();
//        errorResponse.setError("Another error");
//        errorResponse.setMessage("Different message");
//        errorResponse.setTimestamp(now);
//
//        assertEquals("Another error", errorResponse.getError());
//        assertEquals("Different message", errorResponse.getMessage());
//        assertEquals(now, errorResponse.getTimestamp());
//    }

