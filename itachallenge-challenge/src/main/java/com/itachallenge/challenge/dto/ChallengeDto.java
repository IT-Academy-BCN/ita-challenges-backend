package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private UUID challengeId;

    @JsonProperty(value = "challenge_title", index = 1)
    private String title;

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

    @JsonProperty(index = 4)
    private Integer popularity;

    @JsonProperty(index = 5)
    private Float percentage;

    @JsonProperty(index = 6)
    private Set<LanguageDto> languages;

    @JsonProperty(index = 7)
    private List<UUID> solutions;

}
