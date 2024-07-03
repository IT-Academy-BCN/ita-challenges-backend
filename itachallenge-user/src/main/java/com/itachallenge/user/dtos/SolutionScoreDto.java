package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SolutionScoreDto {

    @JsonProperty(value = "uuid_challenge", index = 0)
    private UUID challengeId;

    @JsonProperty(value = "uuid_language", index = 1)
    private UUID languageId;

    @JsonProperty(value = "solution_text", index = 2)
    private String solutionText;

    @JsonProperty(value = "score", index = 3)
    private int score;


}
