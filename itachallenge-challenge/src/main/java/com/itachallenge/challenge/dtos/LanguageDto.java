package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LanguageDto{

    @JsonProperty("id_language")
    private int languageId;

    @JsonProperty("language_name")
    private String languageName;

    public int getLanguageId() {
        return languageId;
    }

    public String getLanguageName() {
        return languageName;
    }
}
