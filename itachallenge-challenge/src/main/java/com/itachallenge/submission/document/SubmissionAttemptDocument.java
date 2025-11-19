package com.itachallenge.submission.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SubmissionAttemptDocument {

    @Id
    @Field(name="id_submission")
    private UUID uuid;

    @Field(name="submission_text")
    private String submissionText;

}
