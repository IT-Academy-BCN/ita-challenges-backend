package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.helpers.ResourceHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootApplication
public class LanguageDtoTest {

    @Autowired
    private ObjectMapper mapper;

    private final String languageJsonPath = "json/Language.json";

    private LanguageDto languageDto;
    @BeforeEach
    void setUp(){
        languageDto = LanguageDtoTest.buildLanguageDto(1, "Javascript");
    }

    @Test
    @DisplayName("Serialization LanguageDto test")
    @SneakyThrows({JsonProcessingException.class, IOException.class})
    void rightSerializationTest(){
        LanguageDto dtoSerializable = languageDto;
        String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dtoSerializable);

        String jsonExpected = new ResourceHelper(languageJsonPath).readResourceAsString();
        assertEquals(jsonExpected,jsonResult);
    }

    @Test
    @DisplayName("Deserialization LanguageDto test")
    @SneakyThrows(IOException.class)
    void rightDeserializationTest(){
        String jsonDeserializable = new ResourceHelper(languageJsonPath).readResourceAsString();
        LanguageDto dtoResult = mapper.readValue(jsonDeserializable, LanguageDto.class);
        //System.out.println(dtoResult);
        LanguageDto dtoExpected = languageDto;
        assertThat(dtoResult).usingRecursiveComparison().isEqualTo(dtoExpected);
    }

    static LanguageDto buildLanguageDto(int languageId, String languageName){
        return new LanguageDto(languageId,languageName);
    }

    static Set<LanguageDto> buildSetLanguages(LanguageDto... languagesDtos){
        Set<LanguageDto> languages = new HashSet<>();
        Collections.addAll(languages, languagesDtos);
        return languages;
    }
}
