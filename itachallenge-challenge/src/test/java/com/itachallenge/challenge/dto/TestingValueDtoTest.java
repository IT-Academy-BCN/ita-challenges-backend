package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.dto.TestingValueDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

class TestingValueDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testBuilderAndValues() {
        List<?> inParams = List.of("input1", "input2");
        List<?> outParams = List.of("output1", "output2");

        TestingValueDto dto = TestingValueDto.builder()
                .inParam(inParams)
                .outParam(outParams)
                .build();

        assertThat(dto.getInParam()).hasSize(inParams.size());
        assertThat(dto.getOutParam()).hasSize(outParams.size());

        for (int i = 0; i < inParams.size(); i++) {
            assertThat(dto.getInParam().get(i)).isEqualTo(inParams.get(i));
        }
        for (int i = 0; i < outParams.size(); i++) {
            assertThat(dto.getOutParam().get(i)).isEqualTo(outParams.get(i));
        }
    }


    @Test
    void testSerialization() throws Exception {
        TestingValueDto dto = TestingValueDto.builder()
                .inParam(List.of("input1", "input2"))
                .outParam(List.of("output1", "output2"))
                .build();

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json)
                .contains("\"in_param\":")
                .contains("\"out_param\":")
                .contains("input1", "output1");
    }

    @Test
    void testDeserialization() throws Exception {
        String json = """
            {
                "in_param": ["input1", "input2"],
                "out_param": ["output1", "output2"]
            }
            """;

        TestingValueDto dto = objectMapper.readValue(json, TestingValueDto.class);

        // Verifica cada elemento individualmente
        Assertions.assertEquals("input1", dto.getInParam().get(0).toString());
        Assertions.assertEquals("input2", dto.getInParam().get(1).toString());
        Assertions.assertEquals("output1", dto.getOutParam().get(0).toString());
        Assertions.assertEquals("output2", dto.getOutParam().get(1).toString());
    }

}