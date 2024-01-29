package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.user.annotations.GenericUUIDValid;
import com.itachallenge.user.enums.ChallengeStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import org.springframework.stereotype.Component;
@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserSolutionDto {

    @JsonProperty(value ="uuid_user")
    @GenericUUIDValid(message = "Invalid UUID")
    private String userId;

    @JsonProperty(value ="uuid_challenge")
    @GenericUUIDValid(message = "Invalid UUID")
    private String challengeId;

    @JsonProperty(value ="uuid_language")
    @GenericUUIDValid(message = "Invalid UUID")
    private String languageId;

    @JsonProperty(value ="status")
    @NotBlank(message = "Status is required")
    private String status;

    @JsonProperty(value ="solution_text")
    @NotBlank(message = "Solution text is required")
    private String solutionText;

    }

