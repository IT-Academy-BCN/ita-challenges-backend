package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
<<<<<<< HEAD
import lombok.Setter;
=======
>>>>>>> 2492a1860466b41be5a07ae3a0e7abc43a023c67

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LanguageDto{

    @JsonProperty(value = "id_language", index = 0)
    private UUID idLanguage;

    @JsonProperty(value = "language_name", index = 1)
    private String languageName;
}
