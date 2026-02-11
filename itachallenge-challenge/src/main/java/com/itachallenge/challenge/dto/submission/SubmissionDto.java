package com.itachallenge.challenge.dto.submission;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubmissionDto {
    @Deprecated(since = "3.2.1", forRemoval = true)
    @Schema(deprecated = true, description = "Deprecated: this field will be removed in a future release.")
    @JsonProperty(value = "uuid_user")
    private String userId;

    @JsonProperty(value = "uuid_challenge")
    private String challengeId;

    @JsonProperty(value = "uuid_language")
    private String languageId;

    @JsonProperty("status")
    private String status;

    @JsonProperty(value = "submission_text")
    private String submissionText;
}