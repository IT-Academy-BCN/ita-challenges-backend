package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserSolutionScoreDto {

    @JsonProperty(value ="uuid_user")
    private String userId;

    @JsonProperty(value ="uuid_challenge")
    private String challengeId;

    @JsonProperty(value ="uuid_language")
    private String languageId;

    @JsonProperty(value ="solution_text")
    private String solutionText;

    @JsonProperty(value = "score")
    private int score;


}
