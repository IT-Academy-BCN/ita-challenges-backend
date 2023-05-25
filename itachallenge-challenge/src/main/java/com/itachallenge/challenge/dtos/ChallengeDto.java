package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeDto {

    //Usar/Ampliar constructores / setters según necesidad
    //Canviar types en caso que facilite inicializar el objeto
    @JsonProperty("id_challenge")
    private UUID challengeId; //main info
    @JsonProperty("challenge_title")
    private String title; //main info
    private String level; //main info
    private List<LanguageDto> languages; //main info
    private String username; //main info
    @JsonProperty("creation_date")
    private Date creationDate; //main info
    private String popularity; //main info
    @JsonProperty("percentage")
    private String percentageStartedAndCompleted; //main info
    private DetailDto details;
    private List<SolutionDto> solutions;
    private List<String> tags; //ids of tags. Allow request the resources related to the challenge.
    private List<String> related; //ids of the challenges relateds (for challenge's relateds section)
}
