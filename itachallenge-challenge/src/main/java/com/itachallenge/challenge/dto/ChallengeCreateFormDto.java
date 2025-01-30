package com.itachallenge.challenge.dto;

import com.itachallenge.challenge.enums.DifficultyLevel;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChallengeCreateFormDto {

    @NotEmpty(message = "cannot be empty")
    private String challengeTitle;

    @NotEmpty(message = "cannot be empty")
    private String description;

    private DifficultyLevel level;

    @NotEmpty(message = "cannot be empty")
    private String language;

    @NotEmpty(message = "cannot be empty")
    private String solution;
}
