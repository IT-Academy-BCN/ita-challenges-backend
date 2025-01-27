package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ChallengeCreateFormDto {

    @JsonProperty(value = "challenge_title")
    private String challengeTitle;

    private String description;

    private String level;

    private String language;

    private String solution;
}
