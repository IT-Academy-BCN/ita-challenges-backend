package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.DetailDocument;
import com.itachallenge.challenge.document.ExampleDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.exception.ConverterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChallengeDocumentToDtoConverterTest {

    private final ChallengeDocumentToDtoConverter converter = new ChallengeDocumentToDtoConverter();

    private ChallengeDocument challengeDoc1;

    private ChallengeDocument challengeDoc2;

    private ChallengeDto challengeDto1;

    private ChallengeDto challengeDto2;

    @BeforeEach
    public void setUp() {

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

        challengeDoc1 = getChallengeMocked(challengeRandomId1, title, level, localDateTime, detail,
                Set.of(languageDoc1, languageDoc2),
                List.of(solutionsRandomId), Set.of(resourcesRandomId), Set.of(relatedChallengesRandomId));

        challengeDoc2 = getChallengeMocked(challengeRandomId2, title, level, localDateTime, detail,
                Set.of(languageDoc1, languageDoc2),
                List.of(solutionsRandomId), Set.of(resourcesRandomId), Set.of(relatedChallengesRandomId));

        challengeDto1 = getChallengeDtoMocked(challengeRandomId1, title, level, creationDate, detail,
                Set.of(languageDto1, languageDto2),
                List.of(solutionsRandomId), Set.of(resourcesRandomId), Set.of(relatedChallengesRandomId),
                popularity, percentage);

        challengeDto2 = getChallengeDtoMocked(challengeRandomId2, title, level, creationDate, detail,
                Set.of(languageDto1, languageDto2),
                List.of(solutionsRandomId), Set.of(resourcesRandomId), Set.of(relatedChallengesRandomId),
                popularity, percentage);
    }

    @Test
    @DisplayName("Conversion from ChallengeDocument to ChallengeDto. Testing 'convertDocumentToDto' method.")
    void testConvertToDto() throws ConverterException {
        ChallengeDocument challengeDocumentMocked = challengeDoc1;
        ChallengeDto resultDto = converter.convertDocumentToDto(challengeDocumentMocked);
        ChallengeDto expectedDto = challengeDto1;

        assertThat(expectedDto).usingRecursiveComparison()
                .ignoringFields("percentage", "popularity")
                .isEqualTo(resultDto);
    }

    @Test
    @DisplayName("Testing Flux conversion. Test convertDocumentFluxToDtoFlux method.")
    void fromFluxDocToFluxDto() {
        ChallengeDocument challengeMock1 = challengeDoc1;
        ChallengeDocument challengeMock2 = challengeDoc2;

        Flux<ChallengeDto> resultDto = converter.convertDocumentFluxToDtoFlux(Flux.just(challengeMock1, challengeMock2));

        StepVerifier.create(resultDto)
                .expectNextMatches(challengeDto -> validateChallengeDto(challengeDto, challengeMock1))
                .expectNextMatches(challengeDto -> validateChallengeDto(challengeDto, challengeMock2))
                .expectComplete()
                .verify();
    }

    private ChallengeDocument getChallengeMocked(UUID challengeId, String title, String level, LocalDateTime creationDate,
                                                 DetailDocument detail, Set<LanguageDocument> languageIS,
                                                 List<UUID> solutions, Set<UUID> resources, Set<UUID> relatedChallenges) {
        ChallengeDocument challengeIMocked = mock(ChallengeDocument.class);
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
                                               List<UUID> solutions, Set<UUID> resources, Set<UUID> relatedChallenges, Integer popularity, Float percentage) {
        ChallengeDto challengeDocMocked = mock(ChallengeDto.class);
        when(challengeDocMocked.getChallengeId()).thenReturn(challengeId);
        when(challengeDocMocked.getTitle()).thenReturn(title);
        when(challengeDocMocked.getLevel()).thenReturn(level);
      //  when(challengeDocMocked.getCreationDate()).thenReturn("2018-09-09");
        when(challengeDocMocked.getCreationDate()).thenReturn(creationDate);
        when(challengeDocMocked.getDetail()).thenReturn(detail);
        when(challengeDocMocked.getLanguages()).thenReturn(languageIS);
        when(challengeDocMocked.getSolutions()).thenReturn(solutions);
        when(challengeDocMocked.getResources()).thenReturn(resources);
        when(challengeDocMocked.getRelatedChallenges()).thenReturn(relatedChallenges);
        when(challengeDocMocked.getPopularity()).thenReturn(popularity);
        when(challengeDocMocked.getPercentage()).thenReturn(percentage);
        return challengeDocMocked;
    }

    private boolean validateChallengeDto(ChallengeDto challengeDto, ChallengeDocument challengeDoc) {
        Set<LanguageDto> languageDtoSet = challengeDoc.getLanguages().stream()
                .map(this::convertLanguageDocumentToDto)
                .collect(Collectors.toSet());

        return challengeDto.getChallengeId() == challengeDoc.getUuid() &&
                challengeDto.getTitle().equalsIgnoreCase(challengeDoc.getTitle()) &&
                challengeDto.getLevel().equalsIgnoreCase(challengeDoc.getLevel()) &&
                challengeDto.getCreationDate().equalsIgnoreCase(getFormattedCreationDateTime(challengeDoc.getCreationDate())) &&
                validateLanguageSet(languageDtoSet, challengeDto.getLanguages());
    }

    private LanguageDto convertLanguageDocumentToDto(LanguageDocument languageDocument) {
        return new LanguageDto(languageDocument.getIdLanguage(), languageDocument.getLanguageName());
    }

    private boolean validateLanguageSet(Set<LanguageDto> languageDtoSet, Set<LanguageDto> languageSet) {
        boolean validate;
        if (languageDtoSet.size() == languageSet.size()) {
            validate = languageDtoSet.stream()
                    .map(LanguageDto::getIdLanguage)
                    .collect(Collectors.toSet())
                    .equals(languageSet.stream()
                            .map(LanguageDto::getIdLanguage)
                            .collect(Collectors.toSet()));
        } else {
            validate = false;
        }
        return validate;
    }

    private String getFormattedCreationDateTime(LocalDateTime creationDateDocument) {

        ZoneId zoneId = ZoneId.of("Europe/Paris");
        ZonedDateTime zonedDateTime = ZonedDateTime.of(creationDateDocument, zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return zonedDateTime.format(formatter);
    }

}
