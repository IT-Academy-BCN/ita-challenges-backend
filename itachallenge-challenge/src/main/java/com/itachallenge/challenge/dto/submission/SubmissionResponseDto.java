package com.itachallenge.challenge.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubmissionResponseDto {

    @JsonProperty(value = "submission_text")
    private String submissionText;

    @JsonProperty(value = "is_solved")
    private boolean isSolved;

    @JsonProperty(value = "times_solved")
    private int timesSolved;

    @JsonProperty("status")
    private String status;
}
