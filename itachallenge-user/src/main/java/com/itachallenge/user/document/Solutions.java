package com.itachallenge.user.document;

import lombok.*;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "solutions")
public class Solutions {

    @MongoId
    @Field(name = "_id")
    private UUID uuid;

    @Field(name = "user_id")
    private UUID userId;

    @Field(name = "challenge_id")
    private UUID challengeId;

    @Field(name = "language_id")
    private UUID languageId;

    @Field(name = "bookmarked")
    private boolean bookmarked;

    @Field(name = "status")
    private String status;

    @Field(name = "solution")
    private Solution[] solution;
}
