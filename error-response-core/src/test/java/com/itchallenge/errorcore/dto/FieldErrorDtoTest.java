package com.itchallenge.errorcore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FieldErrorDtoTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Test
    @DisplayName("Serializes a fully populated FieldErrorDto correctly using builder")
    void shouldSerializeFullyPopulatedDto() throws Exception {
        FieldErrorDto dto = FieldErrorDto.builder()
                .objectName("challengeDto")
                .field("title")
                .message("The title cannot be empty")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"objectName\":\"challengeDto\"")
                .contains("\"field\":\"title\"")
                .contains("\"message\":\"The title cannot be empty\"")
                .startsWith("{").endsWith("}");
    }

    @Test
    @DisplayName("Does not serialize null fields when using @JsonInclude.NON_EMPTY with builder")
    void shouldOmitNullFieldsInJson() throws Exception {
        FieldErrorDto dto = FieldErrorDto.builder()
                .field("difficulty")
                .build();  // objectName and message are null

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"field\":\"difficulty\"")
                .doesNotContain("objectName")
                .doesNotContain("message");
    }

}
