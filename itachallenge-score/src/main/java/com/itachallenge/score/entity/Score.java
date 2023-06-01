package com.itachallenge.score.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="scores")
public class Score {

    @Id
    UUID scoreID;
    UUID userID;
    UUID challengeID;
    UUID solutionID;

}
