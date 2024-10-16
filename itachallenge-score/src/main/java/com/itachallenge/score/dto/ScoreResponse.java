package com.itachallenge.score.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreResponse {
    //atributos
    @JsonProperty("uuid_challenge")
    private UUID uuidChallenge;
    @JsonProperty("uuid_language")
    private UUID uuidLanguage;
    @JsonProperty("solution_text")
    private String solutionText;
    private int score;
}

