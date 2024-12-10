package com.itachallenge.score.dto.zmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ScoreResponseDto {

    @JsonProperty("uuid_challenge")
    private UUID uuidChallenge;

    @JsonProperty("uuid_language")
    private UUID uuidLanguage;

    @JsonProperty("uuid_solution")
    private UUID uuidSolution;

    @JsonProperty("solution_text")
    private String solutionText;

    @JsonProperty("score")
    private int score;

    @JsonProperty("errors")
    private String errors;

    @JsonProperty("Compilation message")
    private String compilationMessage;

    @JsonProperty("Expected result")
    private String expectedResult;

    public static final ScoreResponseDto INTERNAL_SERVER_ERROR_RESPONSE = new ScoreResponseDto(
            null,                                           // uuidChallenge
            null,                                                       // uuidLanguage
            null,                                                       // uuidSolution
            null,                                                       // solutionText
            0,                                                          // score
            null,                                                       // errors
            "An unexpected error occurred. Please try again later.",    // compilationMessage
            null                                                        // expectedResult
    );

    public static final ScoreResponseDto SOLUTION_TEXT_FILTER_FAILED_RESPONSE = new ScoreResponseDto(
            null,                                                   // uuidChallenge
            null,                                                               // uuidLanguage
            null,                                                               // uuidSolution
            null,                                                               // solutionText
            0,                                                                  // score
            null,                                                               // errors
            "The provided solution text did not pass the required filters.",    // compilationMessage
            null                                                                // expectedResult
    );

}