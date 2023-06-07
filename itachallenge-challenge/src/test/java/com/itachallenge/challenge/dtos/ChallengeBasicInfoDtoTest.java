package com.itachallenge.challenge.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.helpers.ResourceHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootApplication
class ChallengeBasicInfoDtoTest {

    @Autowired
    private ObjectMapper mapper;

    private final String basicInfoChallengeV1JsonPath = "json/BasicInfoChallengeV1.json";
    private final String basicInfoChallengeV2JsonPath = "json/BasicInfoChallengeV2.json";

    private ChallengeBasicInfoDto challengeBasicInfoDto;

    @BeforeEach
    void setUp(){
        LanguageDto firstLanguage = LanguageDtoTest.buildLanguageDto(1, "Javascript");
        LanguageDto secondLanguage = LanguageDtoTest.buildLanguageDto(2, "Java");
        Set<LanguageDto> languages = LanguageDtoTest.buildSetLanguages(firstLanguage,secondLanguage);
        challengeBasicInfoDto = buildChallengeBasicInfoDto
                ("Sociis Industries", "EASY", "lun jun 05 12:30:00 2023", 105, 23.58f,languages);
    }

    @Test
    @DisplayName("Serialization ChallengeBasicInfoDto test")
    @SneakyThrows({JsonProcessingException.class, IOException.class})
    void rightSerializationTest(){
        ChallengeBasicInfoDto dtoSerializable = challengeBasicInfoDto;
        String jsonResult = mapper
                .writer(new DefaultPrettyPrinter().withArrayIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE))
                .writeValueAsString(dtoSerializable);
        String jsonExpectedV1 = new ResourceHelper(basicInfoChallengeV1JsonPath).readResourceAsString();
        String jsonExpectedV2 = new ResourceHelper(basicInfoChallengeV2JsonPath).readResourceAsString();
        Assertions.assertTrue(jsonResult.equals(jsonExpectedV1) || jsonResult.equals(jsonExpectedV2));
    }

    @Test
    @DisplayName("Deserialization ChallengeBasicInfoDto test")
    @SneakyThrows(IOException.class)
    void rightDeserializationTest(){
        String jsonDeserializable = new ResourceHelper(basicInfoChallengeV1JsonPath).readResourceAsString();
        ChallengeBasicInfoDto dtoResult = mapper.readValue(jsonDeserializable, ChallengeBasicInfoDto.class);
        ChallengeBasicInfoDto dtoExpected = challengeBasicInfoDto;
        assertThat(dtoResult).usingRecursiveComparison().isEqualTo(dtoExpected);
    }

    static ChallengeBasicInfoDto buildChallengeBasicInfoDto
            (String title, String level, String creationDate, Integer popularity, Float percentage, Set<LanguageDto> languages){
        return ChallengeBasicInfoDto.builder()
                .title(title)
                .level(level)
                .creationDate(creationDate)
                .popularity(popularity)
                .percentage(percentage)
                .languages(languages)
                .build();
    }

}
