package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter

public class RelatedDto {
    @JsonProperty(value = "related_id", index = 0)
    private UUID relatedChallengeId;

    @JsonProperty(value = "related_title", index = 1)
    private String challengeTitle;

    @JsonProperty(value = "related_creation_date", index = 2)
    private String creationDate;

    @JsonProperty(value = "related_level", index = 3)
    private String level;

    @JsonProperty(value = "related_popularity", index = 4)
    private Integer popularity;

    @JsonProperty(value = "related_languages", index = 5)
    private Set<LanguageDto> languages;


}
