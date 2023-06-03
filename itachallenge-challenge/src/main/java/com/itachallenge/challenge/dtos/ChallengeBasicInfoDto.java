package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Set;

@Builder
public class ChallengeBasicInfoDto{

    @JsonProperty(value = "challenge_title", index = 0)
    private String title;

    @JsonProperty(index = 1)
    private String level;

    // TODO: Solventando tema Date
    //@JsonProperty("creation_date", index = 2)
    //private Date creationDate;

    @JsonProperty(index = 3)
    private Integer popularity;

    @JsonProperty(index = 4)
    private Float percentage;

    @JsonProperty(index = 5)
    private Set<LanguageDto> languages;

    //PRIVATE ALL ARGS CONSTRUCTOR: to force instantiation with @Builder
    // TODO: añadir parámetro creationDate*
    private ChallengeBasicInfoDto(String title, String level, Integer popularity, Float percentage, Set<LanguageDto> languages) {
        this.title = title;
        this.level = level;
        //this.creationDate = creationDate;
        this.popularity = popularity;
        this.percentage = percentage;
        this.languages = languages;
    }

    private ChallengeBasicInfoDto() {
        /*
        private no args because:
        required for deserialization. but not needed/used in our logic (at least till now)
         */
    }
}
