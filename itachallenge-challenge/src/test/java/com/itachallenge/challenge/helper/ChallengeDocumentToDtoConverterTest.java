package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.DetailDocument;
import com.itachallenge.challenge.document.ExampleDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.LanguageDto;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

class ChallengeDocumentToDtoConverterTest {

    private final ChallengeDocumentToDtoConverter converter = new ChallengeDocumentToDtoConverter();

    private ChallengeDocument challengeDoc1;

    private ChallengeDocument challengeDoc2;

    private ChallengeDto challengeDto1;

    private ChallengeDto challengeDto2;

    @BeforeEach
    void setUp(){

        UUID challengeRandomId1 = UUID.randomUUID();
        UUID challengeRandomId2 = UUID.randomUUID();
        UUID exampleRandomId1 = UUID.randomUUID();
        UUID exampleRandomId2 = UUID.randomUUID();
        UUID languageRandomId1 = UUID.randomUUID();
        UUID languageRandomId2 = UUID.randomUUID();
        UUID solutionsRandomId1 = UUID.randomUUID();
        UUID solutionsRandomId2 = UUID.randomUUID();
        UUID resourcesRandomId1 = UUID.randomUUID();
        UUID resourcesRandomId2 = UUID.randomUUID();
        UUID relatedChallengesRandomId1 = UUID.randomUUID();
        UUID relatedChallengesRandomId2 = UUID.randomUUID();

        String[] languageNames = new String[]{"name1", "name2"};
        String title = "Java";
        String level = "Hard";
        LocalDateTime localDateTime = LocalDateTime.of(2023, 6, 5, 12, 30, 0);
        String creationDate = "2023-06-05T12:30:00";
        List<ExampleDocument> exampleDocumentList = List.of(new ExampleDocument(exampleRandomId1, "Example text"),
                new ExampleDocument(exampleRandomId2, "Random example"));
        DetailDocument detail = new DetailDocument("Some detail", exampleDocumentList, "Notes");
        Integer popularity = 0;
        Float percentage = 0.0f;

/*        languageDoc1 = new LanguageDocument(languageRandomId1, languageNames[0]);
        languageDoc2 = new LanguageDocument(languageRandomId2, languageNames[1]);

        languageDto1 = new LanguageDto(languageRandomId1, languageNames[0]);
        languageDto2 = new LanguageDto(languageRandomId2, languageNames[1]);*/
    }
}