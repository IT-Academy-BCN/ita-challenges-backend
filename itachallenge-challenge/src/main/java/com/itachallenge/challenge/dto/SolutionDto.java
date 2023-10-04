package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SolutionDto {

    @JsonProperty(value = "id_solution", index = 0)
    private UUID solutionId;

    @JsonProperty(value = "solution", index = 1)
    private String solutionText;

    @JsonProperty(value = "language", index = 2)
    private UUID idLanguage;
}
