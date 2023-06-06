package com.itachallenge.score.document;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.UUID;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Document(collection="scores")
public class Score {

    @MongoId
    UUID scoreID;
    UUID userID;
    UUID challengeID;
    UUID solutionID;

}
