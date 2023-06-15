package com.itachallenge.challenge.helpers;

import com.itachallenge.challenge.documents.ChallengeI;
import com.itachallenge.challenge.documents.LanguageI;
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

    private LanguageI languageIMocked1;

    private ChallengeI challengeIMocked;

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

        languageIMocked1 = getLanguageMocked(idsLanguages[0], languagesNames[0]);
        LanguageI languageIMocked2 = getLanguageMocked(idsLanguages[1], languagesNames[1]);

        challengeIMocked = getChallengeMockedForBasicInfoDto(challengeRandomId, title, level,
                Set.of(languageIMocked1, languageIMocked2),creationDate);

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

    private LanguageI getLanguageMocked(int idLanguage, String languageName){
        LanguageI languageIMocked = Mockito.mock(LanguageI.class);
        when(languageIMocked.getIdLanguage()).thenReturn(idLanguage);
        when(languageIMocked.getLanguageName()).thenReturn(languageName);
        return languageIMocked;
    }

    private ChallengeI getChallengeMockedForBasicInfoDto(UUID challengeId, String title, String level,
                                                         Set<LanguageI> languageIS, LocalDateTime creationDate){
        ChallengeI challengeIMocked = Mockito.mock(ChallengeI.class);
        when(challengeIMocked.getUuid()).thenReturn(challengeId);
        when(challengeIMocked.getTitle()).thenReturn(title);
        when(challengeIMocked.getLevel()).thenReturn(level);
        when(challengeIMocked.getLanguages()).thenReturn(languageIS);
        when(challengeIMocked.getCreationDate()).thenReturn(creationDate);
        return challengeIMocked;
    }

    @Test
    @DisplayName("Give access to available methods when selecting challenge document conversion test")
    void fromChallengeDocumentTest(){
        ChallengeI anyChallengeI = Mockito.mock(ChallengeI.class);
        assertInstanceOf(ChallengeDocConverter.class, converter.from(anyChallengeI));
    }

    @Test
    @DisplayName("Give access to available methods when selecting language document conversion test")
    void fromLanguageDocumentTest(){
        LanguageI anyLanguageI = Mockito.mock(LanguageI.class);
        assertInstanceOf(LanguageDocConverter.class, converter.from(anyLanguageI));
    }

    @Test
    @DisplayName("Conversion from language document to language dto test")
    void fromLanguageToLanguageDtoTest(){
        LanguageI languageIMocked = languageIMocked1;
        LanguageDto resultDto = converter.from(languageIMocked).toLanguageDto();
        LanguageDto expectedDto = languageDto1;
        assertThat(expectedDto).usingRecursiveComparison().isEqualTo(resultDto);
    }

    @Test
    @DisplayName("Conversion from challenge document to challenge dto")
    void fromChallengeToChallengeDto(){
        ChallengeI challengeIMocked = this.challengeIMocked;
        ChallengeDto resultDto = converter.from(challengeIMocked)
                .toChallengeDto(percentage, popularity);
        ChallengeDto expectedDto = challengeDto;
        assertThat(expectedDto).usingRecursiveComparison().isEqualTo(resultDto);
    }
}
