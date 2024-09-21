package com.itachallenge.score.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreResponse {


    @JsonProperty("uuid_challenge")
    private UUID uuidChallenge;
    @JsonProperty("uuid_language")
    private UUID uuidLanguage;
    @JsonProperty("solution_text")
    private String solutionText;
    private int score;
    private String compilation_Message;
    private String expected_Result;

}

