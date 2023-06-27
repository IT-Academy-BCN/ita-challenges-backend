package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ConverterTest {

    @Autowired
    private Converter converter;

    private LanguageDocument languageMocked1;

    private LanguageDocument languageMocked2;

    private ChallengeDocument challengeMocked1;

    private ChallengeDocument challengeMocked2;

    @BeforeEach
    void setUp() {
        UUID challengeRandomId = UUID.randomUUID();
        String title = "title challenge";
        String level = "some level";
        LocalDateTime creationDate = LocalDateTime.of(2023, 6, 5, 12, 30, 0);
        int[] idsLanguages = new int[]{1, 2};
        String[] languageNames = new String[]{"name1", "name2"};

        languageMocked1 = getLanguageMocked(idsLanguages[0], languageNames[0]);
        languageMocked2 = getLanguageMocked(idsLanguages[1], languageNames[1]);

        challengeMocked1 = getChallengeMocked(challengeRandomId, title, level,
                Set.of(languageMocked1, languageMocked2),creationDate);
        challengeMocked2 = getChallengeMocked(challengeRandomId, "another language", "other level",
                Set.of(languageMocked1, languageMocked2), creationDate);

    }

    private LanguageDocument getLanguageMocked(int idLanguage, String languageName){
        LanguageDocument languageIMocked = Mockito.mock(LanguageDocument.class);
        when(languageIMocked.getIdLanguage()).thenReturn(idLanguage);
        when(languageIMocked.getLanguageName()).thenReturn(languageName);
        return languageIMocked;
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

    @Test
    @DisplayName("Conversion from language document to language dto test")
    void fromLanguageDocumentTest() {
        LanguageDocument languageMock1 = languageMocked1;
        LanguageDocument languageMock2 = languageMocked2;

        Flux<LanguageDto> resultDto = converter.fromLanguageToLanguageDto(Flux.just(languageMock1, languageMock2));

        StepVerifier.create(resultDto)
                .expectNextMatches(languageDto -> validateLanguageDto(languageDto, languageMock1))
                .expectNextMatches(languageDto -> validateLanguageDto(languageDto, languageMock2))
                .expectComplete()
                .verify();
    }

    //método que retorna true si campos language son iguales con languageDto
    private boolean validateLanguageDto(LanguageDto languageDto, LanguageDocument language) {
        return languageDto.getLanguageId() == language.getIdLanguage() &&
                languageDto.getLanguageName().equalsIgnoreCase(language.getLanguageName());
    }

    @Test
    @DisplayName("Test conversion from a flux of 1-n challenge documents returning a flux of challenge dto test")
    void fromChallengeDocumentTest() {
        ChallengeDocument challengeMock1 = challengeMocked1;
        ChallengeDocument challengeMock2 = challengeMocked2;

        Flux<ChallengeDto> resultDto = converter.fromChallengeToChallengeDto(Flux.just(challengeMock1, challengeMock2));

        StepVerifier.create(resultDto)
                .expectNextMatches(challengeDto -> validateChallengeDto(challengeDto, challengeMock1))
                .expectNextMatches(challengeDto -> validateChallengeDto(challengeDto, challengeMock2))
                .expectComplete()
                .verify();
    }

    private boolean validateChallengeDto(ChallengeDto challengeDto, ChallengeDocument challenge) {

        return challengeDto.getChallengeId() == challenge.getUuid() &&
                challengeDto.getTitle().equalsIgnoreCase(challenge.getTitle()) &&
                challengeDto.getLevel().equalsIgnoreCase(challenge.getLevel()) &&
                challengeDto.getCreationDate().equalsIgnoreCase(converter.getFormattedCreationDateTime(challenge.getCreationDate())) &&
                validateLanguageSet(challengeDto.getLanguages(), challenge.getLanguages());

    }

    private boolean validateLanguageSet(Set<LanguageDto> languageDtoSet, Set<LanguageDocument> languageSet) {
        boolean validate;

        if (languageDtoSet.size() == languageSet.size()) {
            validate = languageDtoSet.stream()
                    .map(LanguageDto::getLanguageId)
                    .collect(Collectors.toSet())
                    .equals(languageSet.stream()
                            .map(LanguageDocument::getIdLanguage)
                            .collect(Collectors.toSet()));
        } else {
            validate = false;
        }

        return validate;
    }

}
