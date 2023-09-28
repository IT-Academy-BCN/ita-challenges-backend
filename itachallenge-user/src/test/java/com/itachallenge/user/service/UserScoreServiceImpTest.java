package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserScoreDocument;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.exception.UserScoreNotFoundException;
import com.itachallenge.user.helper.Converter;
import com.itachallenge.user.repository.IUserScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;


class UserScoreServiceImpTest {

    @Mock
    private IUserScoreRepository userScoreRepository;
    @InjectMocks
    private UserScoreServiceImp userScoreService;
    @Mock
    private Converter converter;

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

        UserScoreDocument userScoreDocument = new UserScoreDocument(UUID.randomUUID(),userId, idChallenge, idLanguage,true,1,1,solutionDocumentList );
        UserScoreDto userScoreDto = new UserScoreDto();
        SolutionUserDto<UserScoreDto> expectedSolutionUserDto = new SolutionUserDto<>();
        expectedSolutionUserDto.setInfo(0,1,3, new UserScoreDto[]{userScoreDto});

        when(userScoreRepository.findByUserId(userId)).thenReturn(Flux.just(userScoreDocument));
        when(converter.fromUserScoreDocumentToUserScoreDto(any())).thenReturn(Flux.just(userScoreDto));

        Mono<SolutionUserDto<UserScoreDto>> result = userScoreService.getChallengeById(userId.toString(), idChallenge.toString(), idLanguage.toString() );

        StepVerifier.create(result)
                .expectNextMatches(dto -> Arrays.equals(dto.getResults(), expectedSolutionUserDto.getResults()))
                .expectComplete()
                .verify();
    }

    @Test
    void getUserScoreByUserId_ThrowsException (){

        UUID userId_NotFound = UUID.randomUUID();
        UUID idLanguage = UUID.randomUUID();
        UUID idChallenge = UUID.randomUUID();

        when(userScoreRepository.findByUserId(userId_NotFound)).thenReturn(Flux.empty());

        Mono<SolutionUserDto<UserScoreDto>> result = userScoreService.getChallengeById(userId_NotFound.toString(), idChallenge.toString(), idLanguage.toString() );

        StepVerifier.create(result)
                .expectError(UserScoreNotFoundException.class)
                .verify();

    }
}
