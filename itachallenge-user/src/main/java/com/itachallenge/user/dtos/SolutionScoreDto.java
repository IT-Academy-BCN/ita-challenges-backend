package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.user.enums.ChallengeStatus;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

//@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor//(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SolutionScoreDto {

    @JsonProperty(value = "uuid_user", index = 0)
    private UUID userId;

    @JsonProperty(value = "uuid_challenge", index = 1)
    private UUID challengeId;

    @JsonProperty(value = "uuid_language", index = 2)
    private UUID languageId;

    @JsonProperty(value = "uuid_solution", index = 3)
    private UUID solutionId;

    @JsonProperty(value = "status", index = 4)
    private ChallengeStatus status;

    @JsonProperty(value = "score", index = 5)
    private int score;

    @JsonProperty(value = "errors", index = 6)
    private String errors;

}
