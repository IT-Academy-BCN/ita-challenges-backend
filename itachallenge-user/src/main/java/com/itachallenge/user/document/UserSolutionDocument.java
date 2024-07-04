package com.itachallenge.user.document;

import com.itachallenge.user.enums.ChallengeStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Document(collection="solutions")
@Getter
@Setter
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

    @Field("bookmarked")
    private boolean bookmarked;

    @Field("status")
    private ChallengeStatus status;

    @Field("score")
    private int score;

    @Field("solution")
    private List<SolutionDocument> solutionDocument;

}
