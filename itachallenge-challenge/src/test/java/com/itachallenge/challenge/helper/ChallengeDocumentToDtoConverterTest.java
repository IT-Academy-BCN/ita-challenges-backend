package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.*;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.enums.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


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
        UUID languageRandomId1 = UUID.randomUUID();
        UUID languageRandomId2 = UUID.randomUUID();
        UUID solutionsRandomId = UUID.randomUUID();
        UUID tagIdRandom = UUID.randomUUID();

        String[] languageNames = new String[]{"name1", "name2"};
        List<UUID> tags = List.of(tagIdRandom);
        String title = "Title";
        String level = "Hard";
        LocalDateTime localDateTime = LocalDateTime.of(2023, 6, 5, 12, 30, 0);
        String creationDate = "2023-06-05";
        String description = "Some detail";
        DetailDocument detail = new DetailDocument(description);

        Integer popularity = 0;
        Float percentage = 0.0f;

        LanguageDocument languageDoc1 = new LanguageDocument(languageRandomId1, languageNames[0], "https://image-default.com/javascript.png");
        LanguageDocument languageDoc2 = new LanguageDocument(languageRandomId2, languageNames[1], "https://image-default.com/python.png");
        LanguageDto languageDto1 = new LanguageDto(languageRandomId1, languageNames[0], "https://image-default.com/javascript.png");
        LanguageDto languageDto2 = new LanguageDto(languageRandomId2, languageNames[1], "https://image-default.com/python.png");

        Topic topic = Topic.DEBUGGING;
        int timesFavorite = 20;
        int timesBookmark = 30;
        int timesSolved = 40;

        challengeDoc1 = new ChallengeDocument(challengeRandomId1, title, level, localDateTime, detail,
                Set.of(languageDoc1, languageDoc2), List.of(solutionsRandomId), topic, timesFavorite, timesBookmark, timesSolved, tags);

        challengeDoc2 = new ChallengeDocument(challengeRandomId2, title, level, localDateTime, detail,
                Set.of(languageDoc1, languageDoc2), List.of(solutionsRandomId), topic, timesFavorite, timesBookmark, timesSolved, tags);

        challengeDto1 = getChallengeDtoMocked(challengeRandomId1, title, level, creationDate, detail,
                Set.of(languageDto1, languageDto2),
                List.of(solutionsRandomId),
                popularity, percentage, tags);

        challengeDto2 = getChallengeDtoMocked(challengeRandomId2, title, level, creationDate, detail,
                Set.of(languageDto1, languageDto2),
                List.of(solutionsRandomId),
                popularity, percentage, tags);
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



        assertThat(resultDto.count().block()).isEqualTo(Long.valueOf(2));

        assertThat(resultDto.blockFirst()).usingRecursiveComparison()
                .ignoringFields("percentage", "popularity")
                .isEqualTo(challengeDto1);
        assertThat(resultDto.blockLast()).usingRecursiveComparison()
                .ignoringFields("percentage", "popularity")
                .isEqualTo(challengeDto2);
    }

    private ChallengeDto getChallengeDtoMocked(UUID challengeId, String title, String level, String creationDate, DetailDocument detail,
                                               Set<LanguageDto> languages,
                                               List<UUID> solutions, Integer popularity, Float percentage, List<UUID> tags) {
        ChallengeDto challengeDocMocked = mock(ChallengeDto.class);
        when(challengeDocMocked.getChallengeId()).thenReturn(challengeId);
        when(challengeDocMocked.getTitle()).thenReturn(title);
        when(challengeDocMocked.getLevel()).thenReturn(level);
        when(challengeDocMocked.getDetail()).thenReturn(detail);
        when(challengeDocMocked.getCreationDate()).thenReturn(creationDate);
        when(challengeDocMocked.getLanguages()).thenReturn(languages);
        when(challengeDocMocked.getSolutions()).thenReturn(solutions);
        when(challengeDocMocked.getPopularity()).thenReturn(popularity);
        when(challengeDocMocked.getPercentage()).thenReturn(percentage);
        when(challengeDocMocked.getTopic()).thenReturn(Topic.DEBUGGING);
        when(challengeDocMocked.getTimesFavorite()).thenReturn(20);
        when(challengeDocMocked.getTimesBookmark()).thenReturn(30);
        when(challengeDocMocked.getTimesSolved()).thenReturn(40);
        when(challengeDocMocked.getTags()).thenReturn(tags);
        return challengeDocMocked;
    }
}