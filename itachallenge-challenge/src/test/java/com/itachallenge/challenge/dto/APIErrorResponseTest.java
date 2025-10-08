package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class APIErrorResponseTest {

    private ObjectMapper objectMapper;
    private final Logger log = LoggerFactory.getLogger(APIErrorResponseTest.class);
    private static final String LOG_TEMPLATE = "Serialized JSON: {}";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);// ✅ Support for Instant serialization
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Test
    @DisplayName("Serializes APIErrorResponse with multiple FieldErrorDto and metadata correctly")
    void shouldSerializeErrorResponseWithMultipleErrorsAndMetadata() throws Exception {
        APIErrorResponse response = APIErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .message("Validation failed")
                .path("/api/v1/challenges")
                .timestamp(Instant.parse("2025-10-06T09:00:00Z"))
                .errors(List.of(
                        FieldErrorDto.builder()
                                .objectName("challengeDto")
                                .field("title")
                                .message("The title cannot be empty")
                                .build(),
                        FieldErrorDto.builder()
                                .objectName("challengeDto")
                                .field("difficulty")
                                .message("The difficulty is invalid")
                                .build()
                ))
                .build();

        String json = objectMapper.writeValueAsString(response);
        log.info(LOG_TEMPLATE, json);

        assertThat(json)
                .startsWith("{").endsWith("}")
                .contains("\"status\":400")
                .contains("\"error\":\"Bad Request\"")
                .contains("\"message\":\"Validation failed\"")
                .contains("\"path\":\"/api/v1/challenges\"")
                .contains("\"timestamp\":\"2025-10-06T09:00:00Z\"")
                .contains("\"errors\"")
                .contains("\"objectName\":\"challengeDto\"")
                .contains("\"field\":\"title\"")
                .contains("\"message\":\"The title cannot be empty\"")
                .contains("\"field\":\"difficulty\"")
                .contains("\"message\":\"The difficulty is invalid\"");
    }

    @Test
    @DisplayName("Does not serialize empty error list when using @JsonInclude.NON_EMPTY")
    void shouldOmitEmptyErrorsList() throws Exception {
        APIErrorResponse response = APIErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .message("Empty error list")
                .path("/api/v1/challenges")
                .timestamp(Instant.parse("2025-10-06T09:10:00Z"))
                .errors(List.of()) // empty list
                .build();

        String json = objectMapper.writeValueAsString(response);
        log.info(LOG_TEMPLATE, json);

        assertThat(json)
                .doesNotContain("errors")
                .contains("\"status\":400")
                .contains("\"error\":\"Bad Request\"")
                .contains("\"message\":\"Empty error list\"")
                .contains("\"path\":\"/api/v1/challenges\"")
                .contains("\"timestamp\":\"2025-10-06T09:10:00Z\"");
    }
}
