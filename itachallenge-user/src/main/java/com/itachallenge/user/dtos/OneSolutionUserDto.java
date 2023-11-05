package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OneSolutionUserDto {
    @JsonProperty(value = "id_challenge", index = 0)
    private UUID challengeId;
    @JsonProperty(value = "language", index = 1)
    private UUID languageId;
    @JsonProperty(value = "id_user", index = 2)
    private UUID userId;
    @JsonProperty(value = "solution_text", index = 3)
    private String solutionText;
    @JsonProperty(value = "score", index = 4)
    private int score;
}
