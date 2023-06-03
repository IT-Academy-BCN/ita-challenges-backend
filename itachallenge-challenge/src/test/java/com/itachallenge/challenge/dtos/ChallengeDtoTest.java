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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootApplication
public class ChallengeDtoTest {

    @Autowired
    ObjectMapper mapper;

    private final String challengeJsonPathV1 = "json/ChallengeV1.json";

    private final String challengeJsonPathV2 = "json/ChallengeV2.json";

    private ChallengeDto challengeDto;

    @BeforeEach
    void setUp(){
        LanguageDto firstLanguage = LanguageDtoTest.buildLanguageDto(1, "Javascript");
        LanguageDto secondLanguage = LanguageDtoTest.buildLanguageDto(2, "Java");
        Set<LanguageDto> languages = LanguageDtoTest.buildSetLanguages(firstLanguage,secondLanguage);
        ChallengeBasicInfoDto challengeBasicInfoDto = ChallengeBasicInfoDtoTest.buildChallengeBasicInfoDto
                ("Sociis Industries", "EASY", 105, 23.58f,languages);
        challengeDto = buildChallengeDto(UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296"), challengeBasicInfoDto);
    }

    @Test
    @DisplayName("Serialization ChallengeDto test")
    @SneakyThrows({JsonProcessingException.class, IOException.class})
    void rightSerializationTest(){
        ChallengeDto dtoSerializable = challengeDto;
        //System.out.println(dtoSerializable);
        String jsonResult = mapper
                .writer(new DefaultPrettyPrinter().withArrayIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE))
                .writeValueAsString(dtoSerializable);
        //System.out.println(jsonResult);
        String jsonExpectedV1 = new ResourceHelper(challengeJsonPathV1).readResourceAsString();
        String jsonExpectedV2 = new ResourceHelper(challengeJsonPathV2).readResourceAsString();
        //System.out.println(jsonExpectedV1);
        //System.out.println(jsonExpectedV2);
        //order of languages in challenge's basic info doesn't matter
        Assertions.assertTrue(jsonResult.equals(jsonExpectedV1) || jsonResult.equals(jsonExpectedV2));
    }

    @Test
    @DisplayName("Deserialization ChallengeDto test")
    @SneakyThrows(IOException.class)
    void rightDeserializationTest(){
        String jsonDeserializable = new ResourceHelper(challengeJsonPathV1).readResourceAsString();
        ChallengeDto dtoResult = mapper.readValue(jsonDeserializable, ChallengeDto.class);
        //System.out.println(dtoResult);
        ChallengeDto dtoExpected = challengeDto;
        assertThat(dtoResult).usingRecursiveComparison().isEqualTo(dtoExpected);
    }

    static ChallengeDto buildChallengeDto(UUID challengeUuid, ChallengeBasicInfoDto basicInfoDto){
        return ChallengeDto.builder(challengeUuid)
                .basicInfo(basicInfoDto)
                .build();
    }
}
