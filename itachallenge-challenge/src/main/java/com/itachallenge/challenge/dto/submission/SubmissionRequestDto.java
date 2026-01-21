package com.itachallenge.challenge.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

public class SubmissionRequestDto {

    @JsonProperty(value = "uuid_challenge")
    private String challengeId;

    @JsonProperty(value = "uuid_language")
    private String languageId;

    @JsonProperty("action")
    private String action;

    @JsonProperty(value = "submission_text")
    private String submissionText;
}
