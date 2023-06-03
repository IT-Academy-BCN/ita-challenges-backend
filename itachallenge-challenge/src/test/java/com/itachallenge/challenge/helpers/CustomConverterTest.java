package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.dummies.ChallengeDummy;
import com.itachallenge.challenge.documents.dummies.LanguageDummy;
import com.itachallenge.challenge.dtos.ChallengeDto;
import com.itachallenge.challenge.dtos.LanguageDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootApplication
public class CustomConverterTest {

    @Autowired
    private StarterConverter converter;

    @Test
    @DisplayName("Give access to available methods when selecting challenge document conversion test")
    void fromChallengeDocumentTest(){
        assertInstanceOf(ChallengeDocConverter.class, converter.from(any(ChallengeDummy.class)));
    }

    @Test
    @DisplayName("Give access to available methods when selecting language document conversion test")
    void fromLanguageDocumentTest(){
        assertInstanceOf(LanguageDocConverter.class, converter.from(any(LanguageDummy.class)));
    }

    @Test
    @DisplayName("Conversion from language document to language dto test")
    @SneakyThrows(IOException.class)
    void fromLanguageToLanguageDtoTest(){
        LanguageDummy language = new LanguageDummy(1, "Javascript", null);
        LanguageDto result = converter.from(language).toLanguageDto();
        LanguageDto expected = new ResourceHelper("json/Language.json")
                .mapResourceToObject(LanguageDto.class);
        assertEquals(expected,result);
    }

    @Test
    @DisplayName("Conversion from challenge document to challenge dto with only basic info test")
    @SneakyThrows(IOException.class)
    void fromChallengeToChallengeDtoWithOnlyBasicInfoTest(){
        /*
        TODO:
         A) poner el creation date cuando sepamos tipo + formato
         B) comprovar si el assert para el Set es suficiente o hay que modificar
         la manera en que se checkea la "igualdad de contenido"
         */
        Set<LanguageDummy> languages = Set.of(
                new LanguageDummy(1, "Javascript", null),
                new LanguageDummy(2, "Java", null)
        );
        LocalDate creationDate = null;
        ChallengeDummy challenge = new ChallengeDummy(
                UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296"),
                "EASY", "Sociis Industries", languages, creationDate,
                null,null, null, null);

        ChallengeDto dtoResult = converter.from(challenge)
                .toChallengeDtoWithOnlyBasicInfo(23.58f, 105);
        ChallengeDto expectedDtoV1 = new ResourceHelper("json/ChallengeV1.json")
                .mapResourceToObject(ChallengeDto.class);
        ChallengeDto expectedDtoV2 = new ResourceHelper("json/ChallengeV2.json")
                .mapResourceToObject(ChallengeDto.class);
        Assertions.assertTrue(dtoResult.equals(expectedDtoV1) || dtoResult.equals(expectedDtoV2));
    }
}
