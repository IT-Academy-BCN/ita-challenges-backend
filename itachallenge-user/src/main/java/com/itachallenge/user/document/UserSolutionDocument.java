package com.itachallenge.user.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.UUID;
@AllArgsConstructor
@Setter
@Builder
@NoArgsConstructor
@Getter
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

    @Field("bookmarked")
    private boolean bookmarked;

    @Field("status")
    private String status;

    @Field("score")
    private int score;

     @Field("solution")
     private List<SolutionDocument> solutionDocument;
}
