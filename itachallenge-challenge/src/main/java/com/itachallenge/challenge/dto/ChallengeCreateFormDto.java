package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.challenge.enums.DifficultyLevel;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChallengeCreateFormDto {

    @NotEmpty(message = "cannot be empty")
    @JsonProperty(value = "challenge_title")
    private String challengeTitle;

    @NotEmpty(message = "cannot be empty")
    private String description;

    @NotEmpty(message = "cannot be empty")
    private DifficultyLevel level;

    @NotEmpty(message = "cannot be empty")
    private String language;

    @NotEmpty(message = "cannot be empty")
    private String solution;
}
