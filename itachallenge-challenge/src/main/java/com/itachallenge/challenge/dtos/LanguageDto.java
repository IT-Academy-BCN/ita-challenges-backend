package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LanguageDto{

    @JsonProperty(value = "id_language", index = 0)
    private int languageId;

    @JsonProperty(value = "language_name", index = 1)
    private String languageName;
}
