package com.itachallenge.challenge.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
/**
 * DTO for a peer submission in the list returned by GET peer-solutions (Story #191).
 * In this project "Solution" refers to official coach solutions; this endpoint returns student submissions, hence "Submission".
 * <p>
 * Fields and PO justification (#191):
 * <ul>
 *   <li>{@code submission_id} – Identifies the submission; needed for UI and future actions.</li>
 *   <li>{@code challenge_id} – Identifies the challenge; endpoint is "peer solutions for a challenge".</li>
 *   <li>{@code user_id} – Identifies the submitter (for display/context).</li>
 *   <li>{@code language_id} – Language of the submission.</li>
 *   <li>{@code submitted_at} – When submitted; required for "sorted by date descending" (PO).</li>
 *   <li>{@code submission_text} – Solution content (PO: "solution content or repository link").</li>
 *   <li>{@code status} – Submission status.</li>
 *   <li>{@code author} – Student name or alias (PO). Populated from username stored at submitted time (JWT); no call to User micro.</li>
 * </ul>
 * <p>
 * For MVP, fields may be null when data is missing (e.g., legacy submissions, optional values). The API contract allows nulls where documented; consumers should handle null appropriately (e.g., display "Unknown" or omit the field).
 */

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
