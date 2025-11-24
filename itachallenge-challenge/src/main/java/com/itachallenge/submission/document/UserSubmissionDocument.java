package com.itachallenge.submission.document;

import com.itachallenge.submission.enums.UserSubmissionAction;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Document(collection = "submissions")
public class UserSubmissionDocument {

    @Id
    @Field("_id")
    private UUID uuid;

    @Field("user_id")
    private UUID userId;

    @Field("challenge_id")
    private UUID challengeId;

    @Field("language_id")
    private UUID languageId;

    @Field("action")
    private UserSubmissionAction action;

    @Field("submission")
    private SubmissionAttemptDocument submissionAttemptDocument;
}
