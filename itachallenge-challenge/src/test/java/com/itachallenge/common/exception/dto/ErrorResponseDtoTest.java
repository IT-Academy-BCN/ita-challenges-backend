package com.itachallenge.common.exception.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void whenDetailsIsPresent_thenItIsSerialized() throws Exception {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .errorCode("VALIDATION_ERROR")
                .message("Validation failed")
                .timestamp("2026-02-08T12:00:00Z")
                .path("/api/v1/test")
                .details(Map.of("field", "error"))
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"details\"");
        assertThat(json).contains("\"field\":\"error\"");
    }

    @Test
    void whenDetailsIsNull_thenItIsNotSerialized() throws Exception {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .errorCode("VALIDATION_ERROR")
                .message("Validation failed")
                .timestamp("2026-02-08T12:00:00Z")
                .path("/api/v1/test")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).doesNotContain("details");
    }
}
