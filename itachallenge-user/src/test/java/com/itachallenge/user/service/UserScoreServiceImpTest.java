package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.dtos.UserScoreDto;
import com.itachallenge.user.enums.ChallengeStatus;
import com.itachallenge.user.helper.ConverterDocumentToDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserScoreServiceImpTest {

    @Mock
    private IUserSolutionRepository userSolutionRepository;
    @Mock
    private ConverterDocumentToDto converter;
    @InjectMocks
    private UserSolutionServiceImp userSolutionServiceImp;

    private UserSolutionDocument userSolutionDocument;

    private UUID userId;
    private UUID idChallenge;
    private UUID idLanguage;
    private UserScoreDto userScoreDto;
    private SolutionUserDto<UserScoreDto> expectedSolutionUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        idLanguage = UUID.randomUUID();
        idChallenge = UUID.randomUUID();

        SolutionDocument solutionDocument1 = new SolutionDocument(UUID.randomUUID(), "solutionText1");
        SolutionDocument solutionDocument2 = new SolutionDocument(UUID.randomUUID(), "solutionText2");
        SolutionDocument solutionDocument3 = new SolutionDocument(UUID.randomUUID(), "solutionText3");
        List<SolutionDocument> solutionDocumentList = List.of(solutionDocument1, solutionDocument2, solutionDocument3);

        userSolutionDocument = UserSolutionDocument.builder()
                .userId(userId)
                .challengeId(idChallenge)
                .languageId(idLanguage)
                .status(ChallengeStatus.STARTED)
                .solutionDocument(solutionDocumentList)
                .score(1)
                .build();

        userScoreDto = new UserScoreDto();
        expectedSolutionUserDto = new SolutionUserDto<>();
        expectedSolutionUserDto.setInfo(0, 1, 0, new UserScoreDto[]{userScoreDto});
    }

    @Test
    void getUserScoreByUserId() {
        when(userSolutionRepository.findByUserId(any(UUID.class)).thenReturn(Flux.just(userSolutionDocument)));
        when(converter.fromUserScoreDocumentToUserScoreDto(any(UserSolutionDocument.class))).thenAnswer(invocation -> {
            UserSolutionDocument doc = invocation.getArgument(0);
            return Flux.just(new UserScoreDto(doc.getUserId(), doc.getChallengeId(), doc.getLanguageId()));
        });

        // Llama al método del servicio para obtener el resultado
        Mono<SolutionUserDto<UserScoreDto>> result = userSolutionServiceImp.getUserScoreByUserId(userId.toString(), idChallenge.toString(), idLanguage.toString());

        // Verifica el resultado esperado utilizando StepVerifier
        StepVerifier.create(result)
                .expectNextMatches(dto -> Arrays.equals(dto.getResults(), expectedSolutionUserDto.getResults()))
                .expectComplete()
                .verify();
    }
}