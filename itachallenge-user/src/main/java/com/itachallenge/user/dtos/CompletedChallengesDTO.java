package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompletedChallengesDTO {
    @JsonProperty(value = "uuid_challenge")
    private UUID challengeID;

    @JsonProperty(value = "score")
    private int score;
}
