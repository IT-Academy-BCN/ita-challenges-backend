package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.challenge.document.DetailDocument;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ChallengeDto {

    @JsonProperty(value = "uuid_challenge", index = 0)
    private UUID challengeId;

    @JsonProperty(value = "challenge_title", index = 1)
    private Map<Locale, String> title;

    @JsonProperty(index = 2)
    private String level;

    /**
     * Este atributo es String solamente en el DTO.
     * En el document, creationDate es de tipo LocalDateTime.
     * En la clase converter, hay un método privado que convierte y formatea
     * los datos de LocalDateTime a String
     * al formato requerido en el .json
     */
    @JsonProperty(value = "creation_date", index = 3)
    private String creationDate;

    @JsonProperty(value = "detail", index = 4)
    private DetailDocument detail;

    @JsonProperty(index = 5)
    private Integer popularity;

    @JsonProperty(index = 6)
    private Float percentage;

    @JsonProperty(index = 7)
    private Set<LanguageDto> languages;

    @JsonProperty(value = "uuid_language", index = 8)
    private UUID language;

    @JsonProperty(index = 9)
    private List<UUID> solutions;

    @JsonProperty(value = "solutions", index = 10)
    private List<SolutionDto> solutionsDto;



}
