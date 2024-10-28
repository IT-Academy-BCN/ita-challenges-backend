package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.enums.ChallengeStatus;
import com.itachallenge.user.exception.ChallengeNotFoundException;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;


class UserScoreServiceImpTest {

    @Mock
    private IUserSolutionRepository userScoreRepository;
    @InjectMocks
    private UserSolutionServiceImp userScoreService;
    @Mock
    private ConverterDocumentToDto converter;



    //добавила  5 полей
    private UUID userId;
    private UUID challengeId;
    private UUID solutionId;
    private UserSolutionDocument userSolutionDocument;
    private UserScoreDto userScoreDto;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        challengeId = UUID.randomUUID();
        solutionId = UUID.randomUUID();

        // Создаем тестовые объекты
        userSolutionDocument = new UserSolutionDocument();
        userScoreDto = new UserScoreDto();

        // Настраиваем моки
        when(userScoreRepository.findByUserIdAndChallengeIdAndUuid(userId, challengeId, solutionId))
                .thenReturn(Mono.just(userSolutionDocument));
        when(converter.fromUserScoreDocumentToUserScoreDto(any()))
                .thenReturn(Flux.just(userScoreDto));
    }
    @Test
    void testGetSolutionScore_Success() {
        // Вызываем метод
        Mono<UserScoreDto> resultMono = userScoreService.getSolutionScore(userId, challengeId, solutionId);

        // Проверяем результат
        StepVerifier.create(resultMono)
                .expectNext(userScoreDto)
                .verifyComplete();
    }

    @Test
    void testGetSolutionScore_NotFound() {
        // Настраиваем моки для отсутствия данных
        when(userScoreRepository.findByUserIdAndChallengeIdAndUuid(userId, challengeId, solutionId))
                .thenReturn(Mono.empty());

        // Вызываем метод
        Mono<UserScoreDto> resultMono = userScoreService.getSolutionScore(userId, challengeId, solutionId);

        // Проверяем результат на ошибку
        StepVerifier.create(resultMono)
                .expectErrorMatches(throwable -> throwable instanceof ChallengeNotFoundException &&
                        throwable.getMessage().equals("Solution not found"))
                .verify();
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

        UserSolutionDocument userScoreDocument = new UserSolutionDocument(UUID.randomUUID(),userId, idChallenge, idLanguage,true, ChallengeStatus.STARTED,1,solutionDocumentList, "Error 1");
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

}
