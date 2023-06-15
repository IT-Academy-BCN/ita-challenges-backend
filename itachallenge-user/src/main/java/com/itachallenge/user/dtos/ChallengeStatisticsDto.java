package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
////*@AllArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChallengeStatisticsDto {
    @JsonProperty(value = "id_challenge", index = 0)
    private UUID challengeId;

    @JsonProperty(index = 1)
    private int popularity;

    @JsonProperty(index = 2)
    private Float percentage;

}
