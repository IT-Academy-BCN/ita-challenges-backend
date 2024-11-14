package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.challenge.annotations.ValidUUID;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SolutionInfoDto {

    @ValidUUID(message = "Invalid UUID")
    @JsonProperty("uuid_solution")
    private UUID uuid;

    @NotEmpty(message = "cannot be empty")
    @JsonProperty("solution_text")
    private String solutionText;

}
