package com.itachallenge.user.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;
@AllArgsConstructor
@Getter
@Document(collection="user_score")
public class UserScoreDocument {

    @Id
    @Field("_id")
    private UUID uuid;

    @Field("user_id")
    private UUID userId;

    @Field("challenge_id")
    private UUID challengeId;

    @Field("language_id")
    private UUID languajeId;

    @Field("bookmarked")
    private boolean bookmarked;

    @Field("status")
    private int status;

    @Field("score")
    private int score;

    @Field("solution_id")
    private UUID solutionId;

    // @Field("solution_text")
    // private String solutionText;
     @Field("solution")
     private SolutionDocument solutionDocument;

}
