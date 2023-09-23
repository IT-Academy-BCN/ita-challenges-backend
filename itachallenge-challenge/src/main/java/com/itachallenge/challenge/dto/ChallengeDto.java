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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ChallengeDto {

    @JsonProperty(value = "id_challenge", index = 0)
    private UUID uuid;

    @JsonProperty(value = "challenge_title", index = 1)
    private String title;

    @JsonProperty(index = 2)
    private String level;

    @JsonProperty(value = "creation_date", index = 3)
    private String creationDate;

    @JsonProperty(index = 4)
    private DetailDocument detail;

    // Info a obtener del micro score
    @JsonProperty(index = 5)
    private Integer popularity;

    // Info a obtener del micro score
    @JsonProperty(index = 6)
    private Float percentage;

    @JsonProperty(index = 7)
    private Set<LanguageDto> languages;

    @JsonProperty(index = 8)
    private List<UUID> solutions;

    @JsonProperty(index = 9)
    private Set<UUID> resources;

    @JsonProperty(value = "related", index = 10)
    private Set<UUID> relatedChallenges;

    /*
    TODO: ADD more fields "on demand" (when needed)
     */
}
