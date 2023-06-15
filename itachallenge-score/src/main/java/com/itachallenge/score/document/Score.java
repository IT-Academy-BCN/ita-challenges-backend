package com.itachallenge.score.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;


//faltan anotaciones Setter y ToString para cumplir el coverage de sonar
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection="scores")
public class Score {

    @MongoId
    @Field(name = "scoreID")
    private UUID scoreID;
    @Field(name = "userID")
    private UUID userID;
    @Field(name = "challengeID")
    private UUID challengeID;
    @Field(name = "solutionID")
    private UUID solutionID;

}
