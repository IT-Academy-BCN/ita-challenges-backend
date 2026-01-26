package com.itachallenge.challenge.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.submission.enums.SubmissionAction;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import com.itachallenge.challenge.annotations.ValidUUID;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubmissionActionRequestDto {

    @NotNull
    @JsonProperty(value = "uuid_challenge")
    private UUID challengeId;

    @NotNull
    @JsonProperty(value = "uuid_language")
    private UUID languageId;

    @NotNull
    @JsonProperty("action")
    private SubmissionAction action;

    @JsonProperty(value = "submission_text")
    private String submissionText;
}
