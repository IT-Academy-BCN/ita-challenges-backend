package com.itachallenge.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserSolutionResponseDto {

    @JsonProperty(value ="uuid_user")
    private String userId;

    @JsonProperty(value ="uuid_challenge")
    private String challengeId;

    @JsonProperty(value ="uuid_language")
    private String languageId;

    @JsonProperty(value ="solution_text")
    private String solutionText;

    @JsonProperty("status")
    private String status;
}

