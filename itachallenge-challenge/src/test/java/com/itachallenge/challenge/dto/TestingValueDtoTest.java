package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

        Assertions.assertEquals("input1", dto.getInParam().get(0).toString());
        Assertions.assertEquals("input2", dto.getInParam().get(1).toString());
        Assertions.assertEquals("output1", dto.getOutParam().get(0).toString());
        Assertions.assertEquals("output2", dto.getOutParam().get(1).toString());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        List<?> inParams1 = List.of("input1", "input2");
        List<?> outParams1 = List.of("output1", "output2");
        TestingValueDto dto1 = TestingValueDto.builder()
                .inParam(inParams1)
                .outParam(outParams1)
                .build();

        List<?> inParams2 = List.of("input1", "input2");
        List<?> outParams2 = List.of("output1", "output2");
        TestingValueDto dto2 = TestingValueDto.builder()
                .inParam(inParams2)
                .outParam(outParams2)
                .build();

        // Act & Assert
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1).hasSameHashCodeAs(dto2);

        // Arrange
        List<?> inParams3 = List.of("input3", "input4");
        List<?> outParams3 = List.of("output3", "output4");
        TestingValueDto dto3 = TestingValueDto.builder()
                .inParam(inParams3)
                .outParam(outParams3)
                .build();

        // Act & Assert
        assertThat(dto1).isNotEqualTo(dto3);
        assertThat(dto1.hashCode()).isNotEqualTo(dto3.hashCode());
    }

}