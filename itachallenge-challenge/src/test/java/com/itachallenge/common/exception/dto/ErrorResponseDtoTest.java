package com.itachallenge.common.exception.dto;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseDtoTest {

    @Test
    void testErrorResponseDtoBuilderAndGetters() {
        String errorCode = "VALIDATION_ERROR";
        String message = "Validation failed";
        String timestamp = "2026-02-08T12:00:00Z";
        String path = "/api/v1/test";
        Map<String, Object> details = Map.of("field", "error message");

        ErrorResponseDto dto = ErrorResponseDto.builder()
                .errorCode(errorCode)
                .message(message)
                .timestamp(timestamp)
                .path(path)
                .details(details)
                .build();

        assertThat(dto.getErrorCode()).isEqualTo(errorCode);
        assertThat(dto.getMessage()).isEqualTo(message);
        assertThat(dto.getTimestamp()).isEqualTo(timestamp);
        assertThat(dto.getPath()).isEqualTo(path);
        assertThat(dto.getDetails()).isEqualTo(details);
    }

    @Test
    void testErrorResponseDtoNoDetails() {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .errorCode("ERR")
                .build();

        assertThat(dto.getDetails()).isNull();
    }
}
