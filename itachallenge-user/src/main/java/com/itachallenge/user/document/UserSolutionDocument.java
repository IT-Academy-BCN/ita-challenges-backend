package com.itachallenge.user.document;

import com.itachallenge.user.document.enums.ChallengeStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@Document(collection="solutions")
public class UserSolutionDocument {

    @Id
    @Field("_id")
    private UUID uuid;

    @Field("user_id")
    private UUID userId;

    @Field("challenge_id")
    private UUID challengeId;

    @Field("language_id")
    private UUID languageId;

    @Field("status")
    private ChallengeStatus status;

    @Field("solution")
    private SolutionAttemptDocument solutionAttemptDocument;

}


