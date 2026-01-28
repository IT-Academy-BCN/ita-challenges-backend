package com.itachallenge.challenge.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubmissionRequestDto {

    @NotNull
    @JsonProperty(value = "uuid_challenge")
    private UUID challengeId;

    @NotNull
    @JsonProperty(value = "uuid_language")
    private UUID languageId;

    @NotNull
    @JsonProperty("action")
    private String action;

    @NotNull
    @JsonProperty(value = "submission_text")
    private String submissionText;
}
