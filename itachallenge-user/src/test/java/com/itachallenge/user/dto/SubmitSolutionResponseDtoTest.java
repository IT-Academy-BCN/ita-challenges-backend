package com.itachallenge.user.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class SubmitSolutionResponseDtoTest {

    @Test
    void jsonSerialization_includesStatusField_withInProgressStatus() throws Exception {
        SubmitSolutionResponseDto dto = SubmitSolutionResponseDto.builder()
                .solutionText("print('Hola Mundo')")
                .isSolved(true)
                .timesSolved(3)
                .status("IN_PROGRESS")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dto);

        assertTrue(json.contains("\"status\":\"IN_PROGRESS\""));
    }

    @Test
    void jsonSerialization_includesStatusField_withEndedStatus() throws Exception {
        SubmitSolutionResponseDto dto = SubmitSolutionResponseDto.builder()
                .solutionText("print('Hola Mundo')")
                .isSolved(false)
                .timesSolved(3)
                .status("SUBMITTED_COMPLETED")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dto);

        assertTrue(json.contains("\"status\":\"SUBMITTED_COMPLETED\""));
    }
}
