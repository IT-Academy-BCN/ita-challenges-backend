package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SolutionDto {

    @JsonProperty(value = "id_solution", index = 0)
    private UUID uuid;

    @JsonProperty(value = "solution_text", index = 1)
    private String solutionText;

    @JsonProperty(value = "language", index = 2)
    private UUID idLanguage;
}
