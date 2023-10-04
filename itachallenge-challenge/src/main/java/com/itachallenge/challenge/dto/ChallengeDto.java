package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.challenge.document.DetailDocument;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor()
@NoArgsConstructor
@Builder
@Data
public class ChallengeDto{

    @JsonProperty(value = "id_challenge", index = 0)
    private UUID challengeId;

    @JsonProperty(value = "challenge_title", index = 1)
    private String challengeTitle;

    @JsonProperty(index = 2)
    private String challengeLevel;

    @JsonProperty(value = "creation_date", index = 3)
    private String challengeCreationDate;

    @JsonProperty(index = 4)
    private DetailDocument challengeDetail;

    // Info a obtener del micro score
    @JsonProperty(index = 5)
    private Integer challengePopularity;

    // Info a obtener del micro score
    @JsonProperty(index = 6)
    private Float challengePercentage;

    @JsonProperty(index = 7)
    private Set<LanguageDto> challengeLanguages;

    @JsonProperty(index = 8)
    private List<UUID> challengeSolutions;

    @JsonProperty(index = 9)
    private Set<UUID> challengeResources;

    @JsonProperty(value = "related", index = 10)
    private Set<UUID> challengeRelatedChallenges;

    /*
    TODO: ADD more fields "on demand" (when needed)
     */

}
