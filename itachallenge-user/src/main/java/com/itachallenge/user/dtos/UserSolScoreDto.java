package com.itachallenge.user.dtos;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.user.enums.ChallengeStatus;
import lombok.*;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserSolScoreDto {

    @JsonProperty(value = "uuid_user")
    private UUID userId;

    @JsonProperty(value = "uuid_challenge")
    private UUID challengeId;

    @JsonProperty(value = "uuid_language")
    private UUID languageId;

    @JsonProperty(value = "uuid_solution")
    private UUID solutionId;

    @JsonProperty(value = "status")
    private ChallengeStatus status;

    @JsonProperty(value = "score")
    private Integer score;

//    @JsonProperty(value = "errors")
//    private String errors;
}
