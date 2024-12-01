package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CompletedChallengesDTO {
    @JsonProperty(value = "uuid_challenge")
    private UUID challengeId;

    @JsonProperty(value = "score")
    private int score;
}
