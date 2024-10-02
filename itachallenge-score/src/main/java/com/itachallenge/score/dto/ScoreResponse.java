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

    @JsonProperty("Solution text")
    private String solutionText;

    @JsonProperty("User Score")
    private int score;

    @JsonProperty("Compilation message")
    private String compilationMessage;

    @JsonProperty("Expected result")
    private String expectedResult;

}

