package com.itachallenge.challenge.dto;

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
    @DisplayName("Serializa correctamente un FieldErrorDto completo usando builder")
    void shouldSerializeFullyPopulatedDto() throws Exception {
        FieldErrorDto dto = FieldErrorDto.builder()
                .objectName("challengeDto")
                .field("title")
                .message("El título no puede estar vacío")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"objectName\":\"challengeDto\"")
                .contains("\"field\":\"title\"")
                .contains("\"message\":\"El título no puede estar vacío\"");
    }

    @Test
    @DisplayName("No serializa campos nulos cuando se usa @JsonInclude.NON_EMPTY con builder")
    void shouldOmitNullFieldsInJson() throws Exception {
        FieldErrorDto dto = FieldErrorDto.builder()
                .field("difficulty")
                .build();  // objectName y message son null

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"field\":\"difficulty\"")
                .doesNotContain("objectName")
                .doesNotContain("message");
    }

    @Test
    @DisplayName("Serializa sin errores al usar builder")
    void shouldNotThrowExceptionOnSerialization() throws Exception {
        FieldErrorDto dto = FieldErrorDto.builder()
                .objectName("dtoName")
                .field("fieldX")
                .message("Mensaje")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).isNotNull()
                .startsWith("{").endsWith("}");
    }
}
