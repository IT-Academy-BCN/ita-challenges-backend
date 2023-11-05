package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.OneSolutionUserDto;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.helper.ConverterDocumentToDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

import static org.mockito.Mockito.*;

class UserScoreServiceImpTest {

    @Mock
    private IUserSolutionRepository userScoreRepository;
    @InjectMocks
    private UserSolutionServiceImp userScoreService;
    @Mock
    private ConverterDocumentToDto converter;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserScoreByUserId (){

        UUID userId = UUID.randomUUID();
        UUID idLanguage = UUID.randomUUID();
        UUID idChallenge = UUID.randomUUID();

        SolutionDocument solutionDocument1 = new SolutionDocument(UUID.randomUUID(), "solutionText1");
        SolutionDocument solutionDocument2 = new SolutionDocument(UUID.randomUUID(), "solutionText2");
        SolutionDocument solutionDocument3 = new SolutionDocument(UUID.randomUUID(), "solutionText3");
        List<SolutionDocument> solutionDocumentList = List.of(solutionDocument1, solutionDocument2, solutionDocument3);

        UserSolutionDocument userScoreDocument = new UserSolutionDocument(UUID.randomUUID(),userId, idChallenge, idLanguage,true,"medium",1,solutionDocumentList );
        UserScoreDto userScoreDto = new UserScoreDto();
        SolutionUserDto<UserScoreDto> expectedSolutionUserDto = new SolutionUserDto<>();
        expectedSolutionUserDto.setInfo(0,1,0, new UserScoreDto[]{userScoreDto});

        when(userScoreRepository.findByUserId(userId)).thenReturn(Flux.just(userScoreDocument));
        when(converter.fromUserScoreDocumentToUserScoreDto(any())).thenReturn(Flux.just(userScoreDto));

        Mono<SolutionUserDto<UserScoreDto>> result = userScoreService.getChallengeById(userId.toString(), idChallenge.toString(), idLanguage.toString() );

        StepVerifier.create(result)
                .expectNextMatches(dto -> Arrays.equals(dto.getResults(), expectedSolutionUserDto.getResults()))
                .expectComplete()
                .verify();
    }

    @Test
    void postSolutionUserTest() {
        UUID idSolutionUser = UUID.randomUUID();
        UUID idChallenge = UUID.randomUUID();
        UUID idLanguage = UUID.randomUUID();
        UUID idUser = UUID.randomUUID();
        boolean bookmarked = false;
        String status = "status";
        int score = 60;
        List<SolutionDocument> solutionDocuments = new ArrayList<>();
        String solution = "My solution";

        UserSolutionDocument existingDocument = UserSolutionDocument.builder()
                .uuid(idSolutionUser)
                .challengeId(idChallenge)
                .languajeId(idLanguage)
                .userId(idUser)
                .bookmarked(bookmarked)
                .status(status)
                .score(score)
                .solutionDocument(solutionDocuments)
                .build();

        OneSolutionUserDto expectedDto = new OneSolutionUserDto(idChallenge,idLanguage,idUser,solution,score);

        when(userScoreRepository.findByChallengeIdAndLanguajeIdAndUserId(any(),any(),any()))
                .thenReturn(Mono.just(existingDocument));

        when(userScoreRepository.save(any())).thenAnswer(invocation -> {
            UserSolutionDocument savedDocument = invocation.getArgument(0);
            return Mono.just(savedDocument);
        });

        when(converter.fromUserSolutionDocumentToOneSolutionUserDto(any(),any()))
                .thenReturn(Mono.just(expectedDto));

        Mono<OneSolutionUserDto> result = userScoreService.postOneSolutionUser(
                idChallenge.toString(), idLanguage.toString(), idUser.toString(), solution, score);

        StepVerifier.create(result)
                .expectNext(expectedDto)
                .verifyComplete();
    }

}
