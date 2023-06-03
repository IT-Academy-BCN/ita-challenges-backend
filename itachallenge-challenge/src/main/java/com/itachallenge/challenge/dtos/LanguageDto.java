package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LanguageDto{

    @JsonProperty("id_language")
    private int languageId;

    @JsonProperty("language_name")
    private String languageName;

    public LanguageDto(int languageId, String languageName) {
        this.languageId = languageId;
        this.languageName = languageName;
    }

    private LanguageDto() {
        /*
        private no args because:
        required for deserialization. but not needed/used in our logic (at least till now)
         */
    }

}
