package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.user.enums.ChallengeStatus;
import lombok.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserSolutionScoreDto {

    @JsonProperty(value ="uuid_user")
    private String userId;

    @JsonProperty(value ="uuid_challenge")
    private String challengeId;

    @JsonProperty(value ="uuid_language")
    private String languageId;

    @JsonProperty(value ="solution_text")
    private String solutionText;

    @JsonProperty(value = "score")
    private int score;

    @JsonProperty(value = "errors")
    private String errors;
}