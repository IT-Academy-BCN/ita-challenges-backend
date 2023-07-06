package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class LanguageDto{

    @JsonProperty(value = "_id", index = 0)
    private UUID idLanguage;

    @JsonProperty(value = "language_name", index = 1)
    private String languageName;
}
