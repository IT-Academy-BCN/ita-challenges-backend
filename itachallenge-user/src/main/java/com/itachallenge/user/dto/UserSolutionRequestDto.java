package com.itachallenge.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.user.annotations.GenericUUIDValid;
import jakarta.validation.constraints.NotBlank;
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
    @GenericUUIDValid(message = "{validation.uuid.invalid}")
    private String userId;

    @JsonProperty(value ="uuid_challenge")
    @GenericUUIDValid(message = "{user.solution.challengeId.invalid}")
    private String challengeId;

    @JsonProperty(value ="uuid_language")
    @GenericUUIDValid(message = "{user.solution.languageId.invalid}")
    private String languageId;

    @JsonProperty(value ="status")
    private String status;

    @JsonProperty(value ="solution_text")
    @NotBlank(message = "{user.solution.text.notEmpty}")
    private String solutionText;

}


