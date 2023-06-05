package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Set;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeBasicInfoDto{

    @JsonProperty(value = "challenge_title", index = 0)
    private String title;

    @JsonProperty(index = 1)
    private String level;

    // TODO: Solventando tema Date
    @JsonProperty(value = "creation_date", index = 2)
    private String creationDate;

    @JsonProperty(index = 3)
    private Integer popularity;

    @JsonProperty(index = 4)
    private Float percentage;

    @JsonProperty(index = 5)
    private Set<LanguageDto> languages;

    //PRIVATE ALL ARGS CONSTRUCTOR: to force instantiation with @Builder
    // TODO: añadir parámetro creationDate*
    /*private ChallengeBasicInfoDto(String title, String level, LocalDateTime creationDate, Integer popularity, Float percentage, Set<LanguageDto> languages) {
        this.title = title;
        this.level = level;
        this.creationDate = getFormattedCreationDateTime(creationDate);
        this.popularity = popularity;
        this.percentage = percentage;
        this.languages = languages;
    }*/

    private ChallengeBasicInfoDto() {
        /*
        private no args because:
        required for deserialization. but not needed/used in our logic (at least till now)
         */
    }
}
