package com.itachallenge.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.user.annotations.GenericUUIDValid;
import com.itachallenge.user.document.enums.SolutionAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import org.springframework.stereotype.Component;
@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserSolutionRequestDto {

    @JsonProperty(value ="uuid_user")
    @GenericUUIDValid(message = "Invalid UUID")
    private String userId;

    @JsonProperty(value ="uuid_challenge")
    @GenericUUIDValid(message = "Invalid UUID")
    private String challengeId;

    @JsonProperty(value ="uuid_language")
    @GenericUUIDValid(message = "Invalid UUID")
    private String languageId;

    @NotNull(message = "Action cannot be null")
    @JsonProperty(value ="action")
    private SolutionAction action;

    @JsonProperty(value ="solution_text")
    @NotNull(message = "Solution cannot be null")
    private String solutionText;

}


