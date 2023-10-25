package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.DetailDocument;
import com.itachallenge.challenge.document.ExampleDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChallengeDocumentToDtoConverterTest {

    private DocumentToDtoConverter<ChallengeDocument, ChallengeDto> converter;

    private ChallengeDocument challengeDoc1;

    private ChallengeDocument challengeDoc2;

    private ChallengeDto challengeDto1;

    private ChallengeDto challengeDto2;

    @BeforeEach
    public void setUp() {
        converter = new DocumentToDtoConverter<>();

        UUID challengeRandomId1 = UUID.randomUUID();
        UUID challengeRandomId2 = UUID.randomUUID();
        UUID exampleRandomId = UUID.randomUUID();
        UUID languageRandomId1 = UUID.randomUUID();
        UUID languageRandomId2 = UUID.randomUUID();
        UUID solutionsRandomId = UUID.randomUUID();
        UUID resourcesRandomId = UUID.randomUUID();
        UUID relatedChallengesRandomId = UUID.randomUUID();

        String[] languageNames = new String[]{"name1", "name2"};
        String title = "Java";
        String level = "Hard";
        LocalDateTime localDateTime = LocalDateTime.of(2023, 6, 5, 12, 30, 0);
        String creationDate = "2023-06-05";
        List<ExampleDocument> exampleDocumentList = List.of(new ExampleDocument(exampleRandomId, "Example text"),
                new ExampleDocument(exampleRandomId, "Random example"));
        DetailDocument detail = new DetailDocument("Some detail", exampleDocumentList, "Notes");
        Integer popularity = 0;
        Float percentage = 0.0f;

        LanguageDocument languageDoc1 = new LanguageDocument(languageRandomId1, languageNames[0]);
        LanguageDocument languageDoc2 = new LanguageDocument(languageRandomId2, languageNames[1]);
        LanguageDto languageDto1 = new LanguageDto(languageRandomId1, languageNames[0]);
        LanguageDto languageDto2 = new LanguageDto(languageRandomId2, languageNames[1]);

        challengeDoc1 = new ChallengeDocument(challengeRandomId1, title, level, localDateTime, detail,
                Set.of(languageDoc1, languageDoc2),
                List.of(solutionsRandomId), Set.of(resourcesRandomId), Set.of(relatedChallengesRandomId));
        challengeDoc2 = new ChallengeDocument(challengeRandomId2, title, level, localDateTime, detail,
                Set.of(languageDoc1, languageDoc2),
                List.of(solutionsRandomId), Set.of(resourcesRandomId), Set.of(relatedChallengesRandomId));

        challengeDto1 = getChallengeDtoMocked(challengeRandomId1, title, level, creationDate,
                Set.of(languageDto1, languageDto2),
                List.of(solutionsRandomId),
                popularity, percentage);

        challengeDto2 = getChallengeDtoMocked(challengeRandomId2, title, level, creationDate,
                Set.of(languageDto1, languageDto2),
                List.of(solutionsRandomId),
                popularity, percentage);
    }

    @Test
    @DisplayName("Conversion from ChallengeDocument to ChallengeDto. Testing 'convertDocumentToDto' method.")
    void testConvertToDto(){
        ChallengeDocument challengeDocumentMocked = challengeDoc1;
        ChallengeDto resultDto = converter.convertDocumentToDto(challengeDocumentMocked, ChallengeDto.class);
        ChallengeDto expectedDto = challengeDto1;

        assertThat(expectedDto).usingRecursiveComparison()
                .ignoringFields("percentage", "popularity")
                .isEqualTo(resultDto);
    }

    @Test
    @DisplayName("Testing Flux conversion. Test convertDocumentFluxToDtoFlux method.")
    void fromFluxDocToFluxDto() {
        ChallengeDocument challengeDoc1 = this.challengeDoc1;
        ChallengeDocument challengeDoc2 = this.challengeDoc2;

        Flux<ChallengeDto> resultDto = converter.convertDocumentFluxToDtoFlux(Flux.just(challengeDoc1, challengeDoc2), ChallengeDto.class);

        ChallengeDto expectedDto1 = challengeDto1;
        ChallengeDto expectedDto2 = challengeDto2;

        assertThat(resultDto.count().block()).isEqualTo(Long.valueOf(2));

        assertThat(resultDto.blockFirst()).usingRecursiveComparison()
                .ignoringFields("percentage", "popularity")
                .isEqualTo(expectedDto1);
        assertThat(resultDto.blockLast()).usingRecursiveComparison()
                .ignoringFields("percentage", "popularity")
                .isEqualTo(expectedDto2);
    }



    private ChallengeDto getChallengeDtoMocked(UUID challengeId, String title, String level, String creationDate,
                                               Set<LanguageDto> languages,
                                               List<UUID> solutions, Integer popularity, Float percentage) {
        ChallengeDto challengeDocMocked = mock(ChallengeDto.class);
        when(challengeDocMocked.getChallengeId()).thenReturn(challengeId);
        when(challengeDocMocked.getTitle()).thenReturn(title);
        when(challengeDocMocked.getLevel()).thenReturn(level);
        when(challengeDocMocked.getCreationDate()).thenReturn(creationDate);
        when(challengeDocMocked.getLanguages()).thenReturn(languages);
        when(challengeDocMocked.getSolutions()).thenReturn(solutions);
        when(challengeDocMocked.getPopularity()).thenReturn(popularity);
        when(challengeDocMocked.getPercentage()).thenReturn(percentage);
        return challengeDocMocked;
    }

}
