package com.itachallenge.challenge.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @JsonProperty("language_id")
    private String languageId;

    @JsonProperty("submitted_at")
    private LocalDateTime submittedAt;

    @JsonProperty("submission_text")
    private String submissionText;

    @JsonProperty("status")
    private String status;

    @Schema(
            description = "Username of the submission author. Can be null for legacy submissions or when Authorization was missing at submit time.",
            nullable = true
    )
    @JsonProperty("author")
    private String author;
}
