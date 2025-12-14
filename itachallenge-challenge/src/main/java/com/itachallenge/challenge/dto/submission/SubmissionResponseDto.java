package com.itachallenge.challenge.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Builder
@Getter
public class SubmissionResponseDto {
    @JsonProperty(value = "uuid_user")
    private String userId;

    @JsonProperty(value = "uuid_challenge")
    private String challengeId;

    @JsonProperty(value = "uuid_language")
    private String languageId;

    @JsonProperty(value = "submission_text")
    private String submissionText;

    @JsonProperty("status")
    private String status;
}
