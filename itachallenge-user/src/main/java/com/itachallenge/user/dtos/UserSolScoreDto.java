package com.itachallenge.user.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserSolScoreDto {

    @JsonProperty(value = "uuid_Challenge", index = 0)
    private UUID uuidChallenge;

    @JsonProperty(value = "uuid_Language", index = 1)
    private UUID uuidLanguage;

    @JsonProperty(value = "solution_text", index = 2)
    private String solutionText;
}
