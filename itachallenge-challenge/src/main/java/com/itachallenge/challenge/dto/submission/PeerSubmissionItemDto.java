package com.itachallenge.challenge.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PeerSubmissionItemDto {

    @JsonProperty("submission_id")
    private String submissionId;

    @JsonProperty("challenge_id")
    private String challengeId;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("language_id")
    private String languageId;

    @JsonProperty("submitted_at")
    private LocalDateTime submittedAt;

    @JsonProperty("submission_text")
    private String submissionText;

    @JsonProperty("status")
    private String status;

    @JsonProperty("author")
    private String author;
}
