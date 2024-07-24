package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.itachallenge.challenge.document.DetailDocument;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ChallengeDto {

    @JsonProperty(value = "id_challenge", index = 0)
    private UUID challengeId;

    @JsonProperty(value = "uuid_language", index = 1)
    private UUID uuidLanguage;

    @JsonProperty(index = 2)
    private List<SolutionDto> solutions;

    @JsonProperty(value = "challenge_title", index = 3)
    private Map<Locale, String> title;

    @JsonProperty(index = 4)
    private String level;

    /**
     * Este atributo es String solamente en el DTO.
     * En el document, creationDate es de tipo LocalDateTime.
     * En la clase converter, hay un método privado que convierte y formatea
     * los datos de LocalDateTime a String
     * al formato requerido en el .json
     */
    @JsonProperty(value = "creation_date", index = 5)
    private String creationDate;

    @JsonProperty(value = "detail", index = 6)
    private DetailDocument detail;

    @JsonProperty(index = 7)
    private Integer popularity;

    @JsonProperty(index = 8)
    private Float percentage;

    @JsonProperty(index = 9)
    private Set<LanguageDto> languages;

    @JsonProperty(index = 11)
    private List<TestingValueDto> testingValues;




}
