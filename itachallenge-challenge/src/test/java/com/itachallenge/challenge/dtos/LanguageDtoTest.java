package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.helpers.ResourceHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootApplication
public class LanguageDtoTest {

    @Autowired
    private ObjectMapper mapper;

    private final String oneLanguageJsonPath = "json/OneLanguage.json";

    @Test
    @DisplayName("OK Serialization LanguageDto test")
    @SneakyThrows({JsonProcessingException.class, IOException.class})
    void rightSerializationTest(){
        String jsonExpected = new ResourceHelper(oneLanguageJsonPath).readResourceAsString();

        LanguageDto origin = buildLanguageDto(1, "Javascript");
        String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(origin);
        //System.out.println(jsonResult);

        Assertions.assertEquals(jsonExpected,jsonResult);
    }

    @Test
    @DisplayName("OK Deserialization LanguageDto test")
    @SneakyThrows(IOException.class)
    void rightDeserializationTest(){
        LanguageDto expected = buildLanguageDto(1, "Javascript");

        LanguageDto result = new ResourceHelper(oneLanguageJsonPath).mapResourceToObject(LanguageDto.class);
        //System.out.println(result);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    static LanguageDto buildLanguageDto(int languageId, String languageName){
        return new LanguageDto(languageId,languageName);
    }
}
