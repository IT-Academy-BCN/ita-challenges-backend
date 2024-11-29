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


    // Predefined instance constants

    public static final ScoreResponse INTERNAL_SERVER_ERROR_RESPONSE = new ScoreResponse(
            null,                                           // uuidChallenge
            null,                                                       // uuidLanguage
            null,                                                       // solutionText
            0,                                                          // score
            "An unexpected error occurred. Please try again later.",    // compilationMessage
            null                                                        // expectedResult
    );

    public static final ScoreResponse SOLUTION_TEXT_FILTER_FAILED_RESPONSE = new ScoreResponse(
            null,
            null,
            null,
            0,
            "The provided solution text did not pass the required filters.",
            null
    );

}

