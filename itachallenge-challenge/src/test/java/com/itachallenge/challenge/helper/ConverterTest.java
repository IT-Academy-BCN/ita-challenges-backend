package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.Challenge;
import com.itachallenge.challenge.document.Language;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ConverterTest {

    @Autowired
    private Converter converter;

    private Language languageMocked;

    private Challenge challengeMocked;

    private Flux<LanguageDto> languageDto;

    private Flux<ChallengeDto> challengeDto;

    @BeforeEach
    void setUp() {
        UUID challengeRandomId = UUID.randomUUID();
        String title = "title challenge";
        String level = "some level";
        LocalDateTime creationDate = LocalDateTime.of(2023, 6, 5, 12, 30, 0);
        int[] idsLanguages = new int[]{1, 2};
        String[] languageNames = new String[]{"name1", "name2"};

        languageMocked = getLanguageMocked(idsLanguages[0], languageNames[0]);
        Language language2 = getLanguageMocked(idsLanguages[1], languageNames[1]);

        challengeMocked = getChallengeMocked(challengeRandomId, title, level,
                Set.of(languageMocked, language2),creationDate);

        languageDto = Flux.fromIterable(Arrays.asList(
                new LanguageDto(idsLanguages[0], languageNames[0]),
                new LanguageDto(idsLanguages[1], languageNames[1])
        ));

        challengeDto = Flux.fromIterable(Arrays.asList(
                ChallengeDto.builder()
                        .challengeId(challengeRandomId)
                        .title(title)
                        .level(level)
                        .creationDate("2023-06-05T12:30:00+02:00")
                        .languages(Set.of(
                                new LanguageDto(idsLanguages[0], languageNames[0]),
                                new LanguageDto(idsLanguages[1], languageNames[1])
                        ))
                        .build()
        ));

    }

    private Language getLanguageMocked(int idLanguage, String languageName){
        Language languageIMocked = Mockito.mock(Language.class);
        when(languageIMocked.getIdLanguage()).thenReturn(idLanguage);
        when(languageIMocked.getLanguageName()).thenReturn(languageName);
        return languageIMocked;
    }

    private Challenge getChallengeMocked(UUID challengeId, String title, String level,
                                                        Set<Language> languageIS, LocalDateTime creationDate){
        Challenge challengeIMocked = Mockito.mock(Challenge.class);
        when(challengeIMocked.getUuid()).thenReturn(challengeId);
        when(challengeIMocked.getTitle()).thenReturn(title);
        when(challengeIMocked.getLevel()).thenReturn(level);
        when(challengeIMocked.getLanguages()).thenReturn(languageIS);
        when(challengeIMocked.getCreationDate()).thenReturn(creationDate);
        return challengeIMocked;
    }

    @Test
    @DisplayName("Conversion from language document to language dto test")
    void fromLanguageDocumentTest(){
        Language languageMock = languageMocked;
        Flux<LanguageDto> resultDto = converter.fromLanguageToLanguageDto(Flux.just(languageMock));
        Flux<LanguageDto> expectedDto = languageDto;
        assertThat(expectedDto).usingRecursiveComparison().isEqualTo(resultDto);
    }

    @Test
    @DisplayName("Conversion from challenge document to challenge dto test")
    void fromChallengeDocumentTest() {
        Challenge challengeMock = challengeMocked;
        Flux<ChallengeDto> resultDtoFlux = converter.fromChallengeToChallengeDto(Flux.just(challengeMock));
        Flux<ChallengeDto> expectedDtoFlux = challengeDto;
        assertThat(expectedDtoFlux).usingRecursiveComparison().isEqualTo(resultDtoFlux);
    }

}
