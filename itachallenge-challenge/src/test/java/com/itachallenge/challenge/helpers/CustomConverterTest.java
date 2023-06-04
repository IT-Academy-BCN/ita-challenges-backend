package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.dummies.ChallengeDummy;
import com.itachallenge.challenge.documents.dummies.LanguageDummy;
import com.itachallenge.challenge.dtos.ChallengeBasicInfoDto;
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

import java.time.LocalDate;
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

    private LanguageDummy languageMocked1;

    private ChallengeDummy challengeMockedWithOnlyBasicInfo;

    private Float percentage;

    private Integer popularity;

    private LanguageDto languageDto1;

    private ChallengeDto challengeDtoWithOnlyBasicInfo;

    @BeforeEach
    void setUp(){
        UUID challengeId = UUID.randomUUID();
        String title = "title challenge";
        String level = "some level";
        LocalDate creationDate = null; //TODO: fix type and value
        int[] idsLanguages = new int[]{1,2};
        String[] languagesNames = new String[]{"name1","name2"};

        languageMocked1 = getLanguageMocked(idsLanguages[0], languagesNames[0]);
        LanguageDummy languageMocked2 = getLanguageMocked(idsLanguages[1], languagesNames[1]);

        challengeMockedWithOnlyBasicInfo = getChallengeMockedForBasicInfoDto(challengeId, title, level,
                Set.of(languageMocked1,languageMocked2),creationDate);

        percentage = 0.6432f;
        popularity = 25;

        languageDto1 = new LanguageDto(idsLanguages[0], languagesNames[0]);
        challengeDtoWithOnlyBasicInfo = ChallengeDto.builder(challengeId)
                .basicInfo(ChallengeBasicInfoDto.builder()
                        .title(title)
                        .level(level)
                        //.creationDate() //TODO
                        .percentage(percentage)
                        .popularity(popularity)
                        .languages(Set.of(
                                new LanguageDto(idsLanguages[0], languagesNames[0]),
                                new LanguageDto(idsLanguages[1], languagesNames[1])))
                        .build())
                .build();
    }

    private LanguageDummy getLanguageMocked(int idLanguage, String languageName){
        LanguageDummy languageMocked = Mockito.mock(LanguageDummy.class);
        when(languageMocked.getIdLanguage()).thenReturn(idLanguage);
        when(languageMocked.getLanguageName()).thenReturn(languageName);
        return languageMocked;
    }

    private ChallengeDummy getChallengeMockedForBasicInfoDto(UUID challengeId, String title, String level,
                                                             Set<LanguageDummy> languages, LocalDate creationDate){
        ChallengeDummy challengeMocked = Mockito.mock(ChallengeDummy.class);
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
        ChallengeDummy anyChallenge = Mockito.mock(ChallengeDummy.class);
        assertInstanceOf(ChallengeDocConverter.class, converter.from(anyChallenge));
    }

    @Test
    @DisplayName("Give access to available methods when selecting language document conversion test")
    void fromLanguageDocumentTest(){
        LanguageDummy anyLanguage = Mockito.mock(LanguageDummy.class);
        assertInstanceOf(LanguageDocConverter.class, converter.from(anyLanguage));
    }

    @Test
    @DisplayName("Conversion from language document to language dto test")
    void fromLanguageToLanguageDtoTest(){
        LanguageDummy languageMocked = languageMocked1;
        LanguageDto resultDto = converter.from(languageMocked).toLanguageDto();
        LanguageDto expectedDto = languageDto1;
        assertThat(expectedDto).usingRecursiveComparison().isEqualTo(resultDto);
    }

    @Test
    @DisplayName("Conversion from challenge document to challenge dto with only basic info test")
    void fromChallengeToChallengeDtoWithOnlyBasicInfoTest(){
         /*
        TODO:
         A) poner el creation date cuando sepamos tipo + formato
         */
        ChallengeDummy challengeMocked = challengeMockedWithOnlyBasicInfo;
        ChallengeDto resultDto = converter.from(challengeMocked)
                .toChallengeDtoWithOnlyBasicInfo(percentage, popularity);
        ChallengeDto expectedDto = challengeDtoWithOnlyBasicInfo;
        assertThat(expectedDto).usingRecursiveComparison().isEqualTo(resultDto);
    }
}
