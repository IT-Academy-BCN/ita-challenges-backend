package com.itachallenge.score.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreResponse {
    private UUID uuidChallenge;
    private UUID uuidLanguage;
    private String solutionText;
    private int score;
}
