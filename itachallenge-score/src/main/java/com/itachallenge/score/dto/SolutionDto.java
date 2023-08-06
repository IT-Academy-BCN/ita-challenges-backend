package com.itachallenge.score.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class SolutionDto {

    @JsonProperty(value = "uuid_solution", index = 0)
    private UUID uuid;
    @JsonProperty(value = "solution_text", index = 1)
    private String solutionText;


}
