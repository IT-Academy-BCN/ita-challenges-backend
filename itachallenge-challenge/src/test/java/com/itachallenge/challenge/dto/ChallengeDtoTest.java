package com.itachallenge.challenge.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itachallenge.challenge.document.DetailDocument;
import com.itachallenge.challenge.document.ExampleDocument;
import com.itachallenge.challenge.helper.ResourceHelper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ChallengeDtoTest {

    @Autowired
    private ObjectMapper mapper;

    private final String challengeJsonPath = "json/ChallengeSerialized.json";

    private ChallengeDto challengeDtoToSerialize;

    private ChallengeDto challengeDtoFromDeserialization;

    /**
     * Para el test, es necesario pasar como parámetro a creationDate el
     * String equivalente al tipo de formato que devuelve LocalDateTime,
     * ya que en el test lo mapea de nuevo y no coincide formato.
     * Fijarse en archivo BasicInfoChallengeV1.json, ahí tenemos el expected format.
     */
    @BeforeEach
    void setUp(){
        UUID uuid = UUID.fromString("09fabe32-7362-4bfb-ac05-b7bf854c6e0f");
        UUID uuid2 = UUID.fromString("409c9fe8-74de-4db3-81a1-a55280cf92ef");
        UUID exampleRandomId1 = uuid.randomUUID();
        UUID exampleRandomId2 = uuid2.randomUUID();
        LanguageDto firstLanguage = LanguageDtoTest.buildLanguageDto(uuid, "Javascript");
        LanguageDto secondLanguage = LanguageDtoTest.buildLanguageDto(uuid2, "Python");
        Map<Locale, String> titleMap = new HashMap<>();
            titleMap.put(Locale.forLanguageTag("ES"), "Sociis Industries");
            titleMap.put(Locale.forLanguageTag("CA"), "Sociis Industries");
            titleMap.put(Locale.ENGLISH, "Sociis Industries");
        Map<Locale, String> descriptionMap = new HashMap<>();
            descriptionMap.put(Locale.forLanguageTag("ES"), "Descripcíón de prueba");
            descriptionMap.put(Locale.forLanguageTag("CA"), "Descripció de prova");
            descriptionMap.put(Locale.ENGLISH, "Test Description");
        Map<Locale, String> exampleMap1 = new HashMap<>();
            exampleMap1.put(Locale.forLanguageTag("ES"), "Texto de ejemplo");
            exampleMap1.put(Locale.forLanguageTag("CA"), "Texte d'exemple");
            exampleMap1.put(Locale.ENGLISH, "Example text");
        Map<Locale, String> exampleMap2 = new HashMap<>();
            exampleMap2.put(Locale.forLanguageTag("ES"), "Ejemplo random");
            exampleMap2.put(Locale.forLanguageTag("CA"), "Exemple random");
            exampleMap2.put(Locale.ENGLISH, "Random example");
        List<ExampleDocument> exampleDocumentList = List.of(new ExampleDocument(exampleRandomId1, exampleMap1),
                new ExampleDocument(exampleRandomId2, exampleMap2));
        Map<Locale, String> notesMap = new HashMap<>();
            notesMap.put(Locale.forLanguageTag("ES"), "Notas");
            notesMap.put(Locale.forLanguageTag("CA"), "Notes");
            notesMap.put(Locale.ENGLISH, "Notes");
        DetailDocument detail = new DetailDocument(descriptionMap, exampleDocumentList, notesMap);


        challengeDtoToSerialize = buildChallengeWithBasicInfoDto(UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296")
                , titleMap, "EASY", "2023-06-05T12:30:00+02:00", detail,
                105, 23.58f,buildLanguagesSorted(firstLanguage, secondLanguage));

        challengeDtoFromDeserialization = buildChallengeWithBasicInfoDto(UUID.fromString("dcacb291-b4aa-4029-8e9b-284c8ca80296")
                , titleMap, "EASY", "2023-06-05T12:30:00+02:00", detail,
                105, 23.58f,buildLanguages(firstLanguage, secondLanguage));
    }

    @Test
    @DisplayName("Serialization ChallengeDto test")
    @SneakyThrows({JsonProcessingException.class})
    void rightSerializationTest(){
        String jsonResult = mapper
                .writer(new DefaultPrettyPrinter().withArrayIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE))
                .writeValueAsString(challengeDtoToSerialize);
        String jsonExpected = new ResourceHelper(challengeJsonPath).readResourceAsString().orElse(null);
        assertEquals(jsonExpected, jsonResult);
    }

    @Test
    @DisplayName("Deserialization ChallengeDto test")
    @SneakyThrows(IOException.class)
    void rightDeserializationTest(){
        String challengeJsonSource = new ResourceHelper(challengeJsonPath).readResourceAsString().orElse(null);
        ChallengeDto dtoResult = mapper.readValue(challengeJsonSource, ChallengeDto.class);
        assertThat(dtoResult).usingRecursiveComparison().isEqualTo(challengeDtoFromDeserialization);
    }

    static Set<LanguageDto> buildLanguagesSorted(LanguageDto firstLanguage, LanguageDto secondLanguage){
        LinkedHashSet<LanguageDto> languages = new LinkedHashSet<>();
        languages.add(firstLanguage);
        languages.add(secondLanguage);
        return languages;
    }

    static Set<LanguageDto> buildLanguages(LanguageDto firstLanguage, LanguageDto secondLanguage){
        return Set.copyOf(List.of(firstLanguage,secondLanguage));
    }

    static ChallengeDto buildChallengeWithBasicInfoDto
            (UUID id, Map<Locale, String> titleMap, String level, String creationDate, DetailDocument detail,
             Integer popularity, Float percentage, Set<LanguageDto> languages){
        return ChallengeDto.builder()
                .challengeId(id)
                .title(titleMap)
                .level(level)
                .creationDate(creationDate)
                .detail(detail)
                .popularity(popularity)
                .percentage(percentage)
                .languages(languages)
                .build();
    }
}
