package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LanguageDto{

    @JsonProperty(value = "id_language", index = 0)
    private UUID languageId;

    @JsonProperty(value = "language_name", index = 1)
    private String languageName;

    @JsonProperty(value = "language_image", index = 2)
    private String languageImage;

    @JsonSetter("language_image")
    public void setLanguageImage(String languageImage) {
       String defaultImage = "https://default-image.com/default.png";
        this.languageImage = (languageImage != null && !languageImage.trim().isEmpty()) ? languageImage.trim() : defaultImage;
    }
}
