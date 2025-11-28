package com.itachallenge.challenge.dto;

import com.itachallenge.challenge.enums.DifficultyLevel;
import com.itachallenge.challenge.enums.Topic;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class ChallengeCreateDto {

    @NotEmpty(message = "{challenge.title.notEmpty}")
    private String challengeTitle;

    @NotEmpty(message = "{challenge.description.notEmpty}")
    private String description;

    private DifficultyLevel level;

    @NotEmpty(message = "{challenge.language.notEmpty}")
    private String language;

    @NotEmpty(message = "{challenge.solution.notEmpty}")
    private String solution;

    @NotNull(message = "{challenge.topic.notNull}")
    private Topic topic;

    @NotEmpty(message = "{challenge.tags.notEmpty}")
    private List<UUID> tags;
}
