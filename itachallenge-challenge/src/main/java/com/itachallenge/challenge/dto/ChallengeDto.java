package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ChallengeDto {

    @JsonProperty(value = "id_challenge", index = 0)
    private String uuid;

    @JsonProperty(value = "challenge_title", index = 1)
    private String title;

    @JsonProperty(index = 2)
    private String level;

    @JsonProperty(index = 4)
    private Integer popularity;

    @JsonProperty(index = 5)
    private Float percentage;

    @JsonProperty(index = 6)
    private Set<String> languages;

    private Set<String> relatedChallenges;
}
