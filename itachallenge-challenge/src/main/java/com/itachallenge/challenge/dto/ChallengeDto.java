package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ChallengeDto {

    @JsonProperty("id_challenge")
    private UUID uuid;

    @JsonProperty("challenge_title")
    private String title;

    @JsonProperty("languages")
    private Set<LanguageDto> languages;

    @JsonProperty("creation_date")
    private LocalDate creationDate;

    private String username; //can be null if FE knows the username of the logged client

    private Float percentage; //TODO: refactor field name once we know what exactly represents

    private int popularity;

    //@JsonProperty("details")
    //private DetailDto details;

    //private List<SolutionDto> solutions;

    //@JsonProperty("related")
    private Set<UUID> relateds; //uuids, para no embeber un(os) ChallengeDto (bucle casi infinito)

    //private List<ResourceDto> resources;

}
