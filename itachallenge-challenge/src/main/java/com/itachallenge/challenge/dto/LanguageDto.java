package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LanguageDto{

    @JsonProperty(value = "id_language", index = 0)
    private UUID languageId;

    @JsonProperty(value = "language_name", index = 1)
    private String languageName;
}
