package com.itachallenge.submission.document;

import com.itachallenge.submission.enums.SubmissionStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "submissions")
public class SubmissionDocument {

    @Id
    @Field("_id")
    private UUID submissionId;

    @Field("user_id")
    private UUID userId;

    @Field("challenge_id")
    private UUID challengeId;

    @Field("language_id")
    private UUID languageId;

    @Field("status")
    private SubmissionStatus status;

    @Field("submission")
    private String submissionText;

    @Field("created_at")
    private LocalDateTime createdAt;
}
