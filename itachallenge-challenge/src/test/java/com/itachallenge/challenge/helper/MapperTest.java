package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

public class MapperTest {

    private Mapper mapper = Mappers.getMapper(Mapper.class);

    private ChallengeDocument challengeDoc1;

    private ChallengeDocument challengeDoc2;

    private LanguageDocument languageDocument;

    private LanguageDocument languageDocument2;

    @BeforeEach
    void setUp() {
        UUID challengeRandomId = UUID.randomUUID();
        String title = "Random title";
        String level = "some level";
        LocalDateTime creationDate = LocalDateTime.of(2023, 6, 5, 12, 30, 0);
        int[] idsLanguages = new int[]{1, 2};
        String[] languageNames = new String[]{"name1", "name2"};

        languageDocument = getLanguageMocked(idsLanguages[0], languageNames[0]);
        languageDocument2 = getLanguageMocked(idsLanguages[1], languageNames[1]);

        challengeDoc1 = getChallengeMocked(challengeRandomId, title, level,
                Set.of(languageDocument, languageDocument2), creationDate);
        challengeDoc2 = getChallengeMocked(challengeRandomId, "another language", "other level",
                Set.of(languageDocument, languageDocument2), creationDate);
    }

    @Test
    @DisplayName("Conversion from language document to language dto test")
    void fromLanguageDocumentTest() {
        LanguageDocument languageMock1 = languageDocument;
        LanguageDocument languageMock2 = languageDocument2;

        Flux<LanguageDto> resultDto = mapper.toLanguageDtoFromLanguageDocument(Flux.just(languageMock1, languageMock2));

        StepVerifier.create(resultDto)
                .expectNextMatches(languageDto -> validateLanguageDto(languageDto, languageMock1))
                .expectNextMatches(languageDto -> validateLanguageDto(languageDto, languageMock2))
                .expectComplete()
                .verify();
    }

    //método que retorna true si campos language son iguales con languageDto
    private boolean validateLanguageDto(LanguageDto languageDto, LanguageDocument language) {
        return languageDto.getIdLanguage() == language.getIdLanguage() &&
                languageDto.getLanguageName().equalsIgnoreCase(language.getLanguageName());
    }

    @Test
    void fromChallengeDocumentTest() {
        ChallengeDocument challengeMock1 = challengeDoc1;
        ChallengeDocument challengeMock2 = challengeDoc2;

        Flux<ChallengeDto> resultDto = mapper.toDtoFromDoc(Flux.just(challengeMock1, challengeMock2));

        StepVerifier.create(resultDto)
                .expectNextMatches(challengeDto -> validateChallengeDto(challengeDto, challengeMock1))
                .expectNextMatches(challengeDto -> validateChallengeDto(challengeDto, challengeMock2))
                .expectComplete()
                .verify();
    }

    private boolean validateChallengeDto(ChallengeDto challengeDto, ChallengeDocument challenge) {

        return challengeDto.getUuid() == challenge.getUuid() &&
                challengeDto.getTitle().equalsIgnoreCase(challenge.getTitle()) &&
                challengeDto.getLevel().equalsIgnoreCase(challenge.getLevel()) &&
                challengeDto.getCreationDate().equalsIgnoreCase(mapper.mapCreationDateToString(challenge.getCreationDate())) &&
                validateLanguageSet(challengeDto.getLanguages(), challenge.getLanguages());

    }

    private boolean validateLanguageSet(Set<LanguageDto> languageDtoSet, Set<LanguageDocument> languageSet) {
        boolean validate;

        if (languageDtoSet.size() == languageSet.size()) {
            validate = languageDtoSet.stream()
                    .map(LanguageDto::getIdLanguage)
                    .collect(Collectors.toSet())
                    .equals(languageSet.stream()
                            .map(LanguageDocument::getIdLanguage)
                            .collect(Collectors.toSet()));
        } else {
            validate = false;
        }

        return validate;
    }

    private ChallengeDocument getChallengeMocked(UUID challengeId, String title, String level,
                                                 Set<LanguageDocument> languageIS, LocalDateTime creationDate){
        ChallengeDocument challengeIMocked = Mockito.mock(ChallengeDocument.class);
        when(challengeIMocked.getUuid()).thenReturn(challengeId);
        when(challengeIMocked.getTitle()).thenReturn(title);
        when(challengeIMocked.getLevel()).thenReturn(level);
        when(challengeIMocked.getLanguages()).thenReturn(languageIS);
        when(challengeIMocked.getCreationDate()).thenReturn(creationDate);
        return challengeIMocked;
    }

    private LanguageDocument getLanguageMocked(int idLanguage, String languageName){
        LanguageDocument languageIMocked = Mockito.mock(LanguageDocument.class);
        when(languageIMocked.getIdLanguage()).thenReturn(idLanguage);
        when(languageIMocked.getLanguageName()).thenReturn(languageName);
        return languageIMocked;
    }

}
