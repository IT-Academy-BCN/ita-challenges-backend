package com.itachallenge.submission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;
@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserSubmissionResponseDto {
        @JsonProperty(value ="uuid_user")
        private String userId;

        @JsonProperty(value ="uuid_challenge")
        private String challengeId;

        @JsonProperty(value ="uuid_language")
        private String languageId;

        @JsonProperty(value ="submission_text")
        private String submissionText;

        @JsonProperty("action")
        private String action;
    }

