package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SolutionDto {

    @JsonProperty(value = "id_solution", index = 0)
    private UUID uuid;

    @JsonProperty(value = "solution_text", index = 1)
    private String solutionText;

    @JsonProperty(value = "uuid_language", index = 2)
    private UUID idLanguage;

    @JsonProperty(value = "uuid_challenge", index = 3)
    private UUID idChallenge;

    //constructor for testing with uuid, solutionText and idLanguage
    public SolutionDto(UUID uuid, String solutionText, UUID idLanguage) {
        this.uuid = uuid;
        this.solutionText = solutionText;
        this.idLanguage = idLanguage;
    }

}
