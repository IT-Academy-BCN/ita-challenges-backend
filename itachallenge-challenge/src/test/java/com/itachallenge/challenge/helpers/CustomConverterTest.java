package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.documents.Language;
import com.itachallenge.challenge.dtos.ChallengeDto;
import com.itachallenge.challenge.dtos.LanguageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CustomConverterTest {

    @Autowired
    private StarterConverter converter;

    private Language languageMocked1;

    private Challenge challengeMocked;

    private Float percentage;

    private Integer popularity;

    private LanguageDto languageDto1;

    private ChallengeDto challengeDto;

    @BeforeEach
    void setUp(){
        UUID challengeRandomId = UUID.randomUUID();
        String title = "title challenge";
        String level = "some level";
        LocalDateTime creationDate = LocalDateTime.of(2023, 6, 5, 12, 30, 0);
        int[] idsLanguages = new int[]{1,2};
        String[] languagesNames = new String[]{"name1","name2"};

        languageMocked1 = getLanguageMocked(idsLanguages[0], languagesNames[0]);
        Language languageMocked2 = getLanguageMocked(idsLanguages[1], languagesNames[1]);

        challengeMocked = getChallengeMockedForBasicInfoDto(challengeRandomId, title, level,
                Set.of(languageMocked1,languageMocked2),creationDate);

        percentage = 0.6432f;
        popularity = 25;

        languageDto1 = new LanguageDto(idsLanguages[0], languagesNames[0]);
        challengeDto = ChallengeDto.builder()
                .challengeId(challengeRandomId)
                .title(title)
                .level(level)
                .creationDate("2023-06-05T12:30:00+02:00")
                .percentage(percentage)
                .popularity(popularity)
                .languages(Set.of(
                        new LanguageDto(idsLanguages[0], languagesNames[0]),
                        new LanguageDto(idsLanguages[1], languagesNames[1])))
                .build();
    }

    private Language getLanguageMocked(int idLanguage, String languageName){
        Language languageMocked = Mockito.mock(Language.class);
        when(languageMocked.getIdLanguage()).thenReturn(idLanguage);
        when(languageMocked.getLanguageName()).thenReturn(languageName);
        return languageMocked;
    }

    private Challenge getChallengeMockedForBasicInfoDto(UUID challengeId, String title, String level,
                                                             Set<Language> languages, LocalDateTime creationDate){
        Challenge challengeMocked = Mockito.mock(Challenge.class);
        when(challengeMocked.getUuid()).thenReturn(challengeId);
        when(challengeMocked.getTitle()).thenReturn(title);
        when(challengeMocked.getLevel()).thenReturn(level);
        when(challengeMocked.getLanguages()).thenReturn(languages);
        when(challengeMocked.getCreationDate()).thenReturn(creationDate);
        return challengeMocked;
    }

    @Test
    @DisplayName("Give access to available methods when selecting challenge document conversion test")
    void fromChallengeDocumentTest(){
        Challenge anyChallenge = Mockito.mock(Challenge.class);
        assertInstanceOf(ChallengeDocConverter.class, converter.from(anyChallenge));
    }

    @Test
    @DisplayName("Give access to available methods when selecting language document conversion test")
    void fromLanguageDocumentTest(){
        Language anyLanguage = Mockito.mock(Language.class);
        assertInstanceOf(LanguageDocConverter.class, converter.from(anyLanguage));
    }

    @Test
    @DisplayName("Conversion from language document to language dto test")
    void fromLanguageToLanguageDtoTest(){
        Language languageMocked = languageMocked1;
        LanguageDto resultDto = converter.from(languageMocked).toLanguageDto();
        LanguageDto expectedDto = languageDto1;
        assertThat(expectedDto).usingRecursiveComparison().isEqualTo(resultDto);
    }

    @Test
    @DisplayName("Conversion from challenge document to challenge dto")
    void fromChallengeToChallengeDto(){
        Challenge challengeMocked = this.challengeMocked;
        ChallengeDto resultDto = converter.from(challengeMocked)
                .toChallengeDto(percentage, popularity);
        ChallengeDto expectedDto = challengeDto;
        assertThat(expectedDto).usingRecursiveComparison().isEqualTo(resultDto);
    }
}
