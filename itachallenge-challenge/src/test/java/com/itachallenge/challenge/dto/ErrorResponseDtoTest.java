package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseDtoTest {

    private ObjectMapper objectMapper;
    private final Logger log = LoggerFactory.getLogger(ErrorResponseDtoTest.class);

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Test
    @DisplayName("Serializes ErrorResponseDto with multiple FieldErrorDto and metadata correctly")
    void shouldSerializeErrorResponseWithMultipleErrorsAndMetadata() throws Exception {
        ErrorResponseDto response = ErrorResponseDto.builder()
                .status(400)
                .error("Bad Request")
                .message("Validation failed")
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

        assertThat(json)
                .contains("\"status\":400")
                .contains("\"error\":\"Bad Request\"")
                .contains("\"message\":\"Validation failed\"")
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
        ErrorResponseDto response = ErrorResponseDto.builder()
                .status(400)
                .error("Bad Request")
                .message("Empty error list")
                .errors(List.of()) // empty list
                .build();

        String json = objectMapper.writeValueAsString(response);
        log.info("Serialized JSON: {}", json);
        assertThat(json)
                .doesNotContain("errors")
                .contains("\"status\":400")
                .contains("\"error\":\"Bad Request\"")
                .contains("\"message\":\"Empty error list\"");
    }

    @Test
    @DisplayName("Serializes valid JSON structure with minimal metadata and one FieldErrorDto")
    void shouldSerializeValidJson() throws Exception {
        ErrorResponseDto response = ErrorResponseDto.builder()
                .status(400)
                .error("Bad Request")
                .message("Some fields are invalid")
                .errors(List.of(
                        FieldErrorDto.builder()
                                .objectName("dtoName")
                                .field("fieldX")
                                .message("Some message")
                                .build()
                ))
                .build();

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).isNotNull()
                .startsWith("{").endsWith("}")
                .contains("\"status\":400")
                .contains("\"error\":\"Bad Request\"")
                .contains("\"message\":\"Some fields are invalid\"")
                .contains("\"field\":\"fieldX\"");
    }
}
