package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.DetailDocument;
import com.itachallenge.challenge.document.ExampleDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.exception.ConverterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

class SampleDocConverterTest {

    private SampleDocConverter converter = new SampleDocConverter();

    private ChallengeDocument challengeDoc1;

    private ChallengeDocument challengeDoc2;

    private ChallengeDto challengeDto;

    private LanguageDocument languageDocument;

    private LanguageDocument languageDocument2;

    @BeforeEach
    public void setup() {
        // Challenge attributes
        UUID challengeRandomId = UUID.randomUUID();
        UUID languageRandomId1 = UUID.randomUUID();
        UUID languageRandomId2 = UUID.randomUUID();
        UUID exampleRandomId = UUID.randomUUID();
        UUID resourcesRandomId = UUID.randomUUID();
        UUID solutionsRandomId = UUID.randomUUID();
        UUID relatedChallengesRandomId = UUID.randomUUID();
        String title = "Random title";
        String level = "some level";
        LocalDateTime creationDate = LocalDateTime.of(2023, 6, 5, 12, 30, 0);
        String creationDateString = "2018-09-09T11:43:54+0200";
        String[] languageNames = new String[]{"name1", "name2"};
        List<ExampleDocument> exampleDocumentList = List.of(new ExampleDocument(exampleRandomId, "Example text"),
                new ExampleDocument(exampleRandomId, "Random example"));
        DetailDocument detail = new DetailDocument("Some detail", exampleDocumentList, "Notes");

        languageDocument = getLanguageMocked(languageRandomId1, languageNames[0]);
        languageDocument2 = getLanguageMocked(languageRandomId2, languageNames[1]);

        challengeDoc1 = getChallengeMocked(challengeRandomId, title, level,
                creationDate, detail, Set.of(languageDocument, languageDocument2),
                Set.of(solutionsRandomId), Set.of(resourcesRandomId), Set.of(relatedChallengesRandomId));

        challengeDoc2 = getChallengeMocked(challengeRandomId, "another title", "another level",
                creationDate, detail, Set.of(languageDocument, languageDocument2),
                Set.of(solutionsRandomId), Set.of(resourcesRandomId), Set.of(relatedChallengesRandomId));

        challengeDto = getChallengeDtoMocked(challengeRandomId, title, level,
                creationDateString, detail, Set.of(getLanguageDtoMocked(languageRandomId1, languageNames[0]), getLanguageDtoMocked(languageRandomId2, languageNames[1])),
                Set.of(solutionsRandomId), Set.of(resourcesRandomId), Set.of(relatedChallengesRandomId));
    }

    @Test
    void testConvertToDoc() throws ConverterException {
        ChallengeDto challengeMocked = challengeDto;
        ChallengeDocument resultDoc = converter.convert(challengeMocked);
        ChallengeDocument expectedDoc = challengeDoc1;
        assertThat(expectedDoc).usingRecursiveComparison().isEqualTo(resultDoc);

    }

    private ChallengeDocument getChallengeMocked(UUID challengeId, String title, String level, LocalDateTime creationDate,
                                                 DetailDocument detail, Set<LanguageDocument> languageIS,
                                                 Set<UUID> solutions, Set<UUID> resources, Set<UUID> relatedChallenges){
        ChallengeDocument challengeIMocked = Mockito.mock(ChallengeDocument.class);
        when(challengeIMocked.getUuid()).thenReturn(challengeId);
        when(challengeIMocked.getTitle()).thenReturn(title);
        when(challengeIMocked.getLevel()).thenReturn(level);
        when(challengeIMocked.getCreationDate()).thenReturn(creationDate);
        when(challengeIMocked.getDetail()).thenReturn(detail);
        when(challengeIMocked.getLanguages()).thenReturn(languageIS);
        when(challengeIMocked.getSolutions()).thenReturn(solutions);
        when(challengeIMocked.getResources()).thenReturn(resources);
        when(challengeIMocked.getRelatedChallenges()).thenReturn(relatedChallenges);
        return challengeIMocked;
    }

    private ChallengeDto getChallengeDtoMocked(UUID challengeId, String title, String level, String creationDate,
                                               DetailDocument detail, Set<LanguageDto> languageIS,
                                               Set<UUID> solutions, Set<UUID> resources, Set<UUID> relatedChallenges){
        ChallengeDto challengeDocMocked = Mockito.mock(ChallengeDto.class);
        when(challengeDocMocked.getUuid()).thenReturn(challengeId);
        when(challengeDocMocked.getTitle()).thenReturn(title);
        when(challengeDocMocked.getLevel()).thenReturn(level);
        when(challengeDocMocked.getCreationDate()).thenReturn("2018-09-09T11:43:54+0200");
        when(challengeDocMocked.getCreationDate()).thenReturn(creationDate);
        when(challengeDocMocked.getDetail()).thenReturn(detail);
        when(challengeDocMocked.getLanguages()).thenReturn(languageIS);
        when(challengeDocMocked.getSolutions()).thenReturn(solutions);
        when(challengeDocMocked.getResources()).thenReturn(resources);
        when(challengeDocMocked.getRelatedChallenges()).thenReturn(relatedChallenges);
        return challengeDocMocked;
    }

    private LanguageDocument getLanguageMocked(UUID idLanguage, String languageName){
        LanguageDocument languageIMocked = Mockito.mock(LanguageDocument.class);
        when(languageIMocked.getIdLanguage()).thenReturn(idLanguage);
        when(languageIMocked.getLanguageName()).thenReturn(languageName);
        return languageIMocked;
    }

    private LanguageDto getLanguageDtoMocked(UUID idLanguage, String languageName){
        LanguageDto languageIMocked = Mockito.mock(LanguageDto.class);
        when(languageIMocked.getIdLanguage()).thenReturn(idLanguage);
        when(languageIMocked.getLanguageName()).thenReturn(languageName);
        return languageIMocked;
    }

}
