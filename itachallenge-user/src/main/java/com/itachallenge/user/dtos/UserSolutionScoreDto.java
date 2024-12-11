package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.user.annotations.GenericUUIDValid;
import lombok.*;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserSolutionScoreDto {

    @JsonProperty(value = "uuid_user")
    @GenericUUIDValid(message = "Invalid UUID")
    private String userId;

    @JsonProperty(value = "uuid_challenge")
    @GenericUUIDValid(message = "Invalid UUID")
    private String challengeId;

    @JsonProperty(value = "uuid_language")
    @GenericUUIDValid(message = "Invalid UUID")
    private String languageId;

    @JsonProperty(value = "solution_text")
    private String solutionText;

    @JsonProperty(value = "score")
    private int score;

    @JsonProperty(value = "errors")
    private String errors;

    @JsonProperty(value = "status")
    private String status;


    public Object getStatus() {
        return status;
    }
}