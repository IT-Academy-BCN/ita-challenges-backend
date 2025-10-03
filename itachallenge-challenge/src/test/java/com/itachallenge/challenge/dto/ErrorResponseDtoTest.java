package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorResponseDtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Test
    @DisplayName("Serializes ErrorResponseDto with multiple FieldErrorDto correctly")
    void shouldSerializeErrorResponseWithMultipleErrors() throws Exception {
        ErrorResponseDto response = ErrorResponseDto.builder()
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
                .errors(List.of()) // empty list
                .build();

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).doesNotContain("errors");
    }

    @Test
    @DisplayName("Serializes valid JSON without errors")
    void shouldSerializeValidJson() throws Exception {
        ErrorResponseDto response = ErrorResponseDto.builder()
                .errors(List.of(
                        FieldErrorDto.builder()
                                .objectName("dtoName")
                                .field("fieldX")
                                .message("Some message")
                                .build()
                ))
                .build();

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).isNotNull();
        assertThat(json).startsWith("{").endsWith("}");
    }
}
