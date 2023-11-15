package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserSolutionScoreDto {

    @JsonProperty(value = "id_challenge", index = 0)
    private UUID challengeId;

    @JsonProperty(value = "language", index = 1)
    private UUID languageID;

    @JsonProperty(value = "id_user", index = 3)
    private UUID userId;

    private String solutionText;

    private int score;


}
