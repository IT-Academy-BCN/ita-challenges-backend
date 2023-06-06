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

    /**
     * Este atributo es String solamente en el DTO.
     * En el document, creationDate es de tipo LocalDateTime.
     * En la clase converter, hay un método privado que convierte y formatea los datos de LocalDateTime
     * al formato requerido en el .json
     */
    @JsonProperty(value = "creation_date", index = 2)
    private String creationDate;

    @JsonProperty(index = 3)
    private Integer popularity;

    @JsonProperty(index = 4)
    private Float percentage;

    @JsonProperty(index = 5)
    private Set<LanguageDto> languages;

    private ChallengeBasicInfoDto() {
        /*
        private no args because:
        required for deserialization. but not needed/used in our logic (at least till now)
         */
    }
}
