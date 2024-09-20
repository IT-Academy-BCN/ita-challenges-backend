package com.itachallenge.score.document;

import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Score {

    private UUID scoreID;
    private UUID userID;
    private UUID challengeID;
    private UUID solutionID;
}
