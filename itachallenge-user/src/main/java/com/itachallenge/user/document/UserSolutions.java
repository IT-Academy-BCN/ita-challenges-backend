package com.itachallenge.user.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "userSolutions")
public class UserSolutions {

    @Id
    @Field(name = "_id")
    @JsonProperty("_id")
    private UUID uuid;

    @Field(name = "user_id")
    @JsonProperty("user_id")
    private UUID userId;

    @Field(name = "challenge_id")
    @JsonProperty("challenge_id")
    private UUID challengeId;

    @Field(name = "language_id")
    @JsonProperty("language_id")
    private UUID languageId;

    @Field(name = "bookmarked")
    private boolean bookmarked;

    @Field(name = "status")
    private String status;

    @Field(name = "score")
    private int score;

    @Field(name = "solution")
    private Solution[] solution;
}
