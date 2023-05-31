package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.challenge.dtos.team.ChallangeBasicInfoDtoI;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class ChallengeBasicInfoDto implements ChallangeBasicInfoDtoI {

    @JsonProperty("challenge_title")
    private String title;

    private String level;

    @JsonProperty("creation_date")
    private LocalDateTime creationDate;

    private Integer popularity;

    private Set<LanguageDto> languages;

    @JsonSerialize(using = PercentageSerializer.class)
    private Float percentage;

    //PRIVATE NO ARGS CONSTRUCTOR: to force instantiation with @Builder
    private ChallengeBasicInfoDto(String title, String level, LocalDateTime creationDate, Integer popularity, Set<LanguageDto> languages, Float percentage) {
        this.title = title;
        this.level = level;
        this.creationDate = creationDate;
        this.popularity = popularity;
        this.languages = languages;
        this.percentage = percentage;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private void setLevel(String level) {
        this.level = level;
    }

    private void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    private void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    private void setLanguages(Set<LanguageDto> languages) {
        this.languages = languages;
    }

    private void setPercentage(Float percentage) {
        this.percentage = percentage;
    }
}
