package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LanguageDto{

    @JsonProperty(value = "id_language", index = 0)
    private int languageId;

    @JsonProperty(value = "language_name", index = 1)
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
