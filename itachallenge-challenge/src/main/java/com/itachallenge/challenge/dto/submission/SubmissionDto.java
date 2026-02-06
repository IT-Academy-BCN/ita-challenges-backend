package com.itachallenge.challenge.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubmissionDto {
    @Deprecated
    @JsonProperty(value = "uuid_user")
    private String userId;
    /**Deprecated: this field will be removed in a future release.
     * Frontend must stop relying on uuid_user before removal.
     */
    @JsonProperty(value = "uuid_challenge")
    private String challengeId;

    @JsonProperty(value = "uuid_language")
    private String languageId;

    @JsonProperty("status")
    private String status;

    @JsonProperty(value = "submission_text")
    private String submissionText;
}
