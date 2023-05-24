package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LanguageDto {
    //Usar/Ampliar constructores / setters según necesidad
    //Canviar types en caso que facilite inicializar el objeto
    @JsonProperty("id_language")
    private String languageId; //main info
    @JsonProperty("language_name")
    private String name; //main info
}
