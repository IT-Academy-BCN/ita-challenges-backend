package com.itachallenge.score.document;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Score {

    private UUID scoreID;
    private UUID userID;
    private UUID challengeID;
    private UUID solutionID;
}
