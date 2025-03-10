package com.itachallenge.challenge.dto;

import com.itachallenge.challenge.enums.DifficultyLevel;
import com.itachallenge.challenge.enums.Topic;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChallengeCreateDto {

    @NotEmpty(message = "cannot be empty")
    private String challengeTitle;

    @NotEmpty(message = "cannot be empty")
    private String description;

    private DifficultyLevel level;

    @NotEmpty(message = "cannot be empty")
    private String language;

    @NotEmpty(message = "cannot be empty")
    private String solution;

    @NotEmpty(message = "cannot be empty")
    private String mentorUsername;

    @NotNull(message = "cannot be empty")
    private Topic topic;
}
