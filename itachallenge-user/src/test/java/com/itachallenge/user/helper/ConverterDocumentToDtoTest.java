package com.itachallenge.user.helper;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.UserScoreDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ConverterDocumentToDtoTest {

    @Autowired
    private ConverterDocumentToDto converter;

    UUID uuid_1 = UUID.randomUUID();
    UUID idUser = UUID.randomUUID();
    UUID idLanguage = UUID.randomUUID();
    UUID idChallenge = UUID.randomUUID();
    String solutionText1 = "Ipsum.. 1";
    String solutionText2 = "Ipsum.. 2";
    SolutionDocument solutionDocument1 = new SolutionDocument(UUID.randomUUID(), solutionText1);
    SolutionDocument solutionDocument2 = new SolutionDocument(UUID.randomUUID(), solutionText2);
    SolutionDocument solutionDocument3 = new SolutionDocument(UUID.randomUUID(), solutionText2);
    List<SolutionDocument> solutionDocumentList = List.of(solutionDocument1, solutionDocument2, solutionDocument3);
    UserSolutionDocument userScoreDocument = new UserSolutionDocument(uuid_1, idUser, idChallenge, idLanguage, true, "medium", 90, solutionDocumentList);

    @DisplayName("Convertir un objeto UserScoreDocument en un objeto UserScoreDto")
    @Test
    void fromUserScoreDocumentToUserScoreDtoTest(){

        Flux<UserScoreDto> result = converter.fromUserScoreDocumentToUserScoreDto(Flux.just(userScoreDocument));

        StepVerifier.create(result)
                .expectNextMatches(userScoreDto -> ValidateUserScoreDto(userScoreDto,userScoreDocument))
                .expectComplete()
                .verify();
    }

    private boolean ValidateUserScoreDto(UserScoreDto userScoreDto, UserSolutionDocument userScoreDocument) {

        return userScoreDto.getUserId().equals(userScoreDocument.getUserId()) &&
                userScoreDto.getChallengeId().equals(userScoreDocument.getChallengeId()) &&
                userScoreDto.getSolutions().equals(userScoreDocument.getSolutionDocument()) &&
                userScoreDto.getLanguageID().equals(userScoreDocument.getLanguageId());
    }
}
