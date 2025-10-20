package com.itachallenge.user.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class APIErrorResponseTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
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

    @Test
    void shouldSerializeToJsonWithoutEmptyFields() throws JsonProcessingException {
        Instant now = Instant.parse("2025-10-15T10:00:00Z");

        APIErrorResponse response = APIErrorResponse.builder()
                .timestamp(now)
                .status(404)
                .error("Not Found")
                .message("User not found")
                .path("/api/v1/user")
                .build();

        String json = objectMapper.writeValueAsString(response);
        assertThat(json)
                .contains("\"status\":404")
                .contains("\"error\":\"Not Found\"")
                .contains("\"path\":\"/api/v1/user\"")
                .doesNotContain("errors");
    }

    @Test
    void shouldIncludeErrorsWhenPresent() throws JsonProcessingException {
        Instant now = Instant.parse("2025-10-15T10:00:00Z");
        FieldErrorDto fieldError = new FieldErrorDto("AdminCreateUserRequestDto","username", "must not be empty");

        APIErrorResponse response = APIErrorResponse.builder()
                .timestamp(now)
                .status(400)
                .error("Bad Request")
                .message("Validation failed")
                .path("/api/register")
                .errors(List.of(fieldError))
                .build();
        String json = objectMapper.writeValueAsString(response);
        assertThat(json)
                .contains("\"errors\"")
                .contains("username")
                .contains("not be empty");
    }
}
