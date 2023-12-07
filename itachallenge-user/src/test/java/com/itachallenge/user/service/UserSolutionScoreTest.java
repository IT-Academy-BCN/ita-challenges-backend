package com.itachallenge.user.service;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.UserSolutionScoreDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
public class UserSolutionScoreTest {

    @Mock
    private IUserSolutionRepository userSolutionRepository;

    @InjectMocks
    private UserSolutionServiceImp userSolutionService;
    ;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ;
    }

    @Test
    public void testAddSolution() {

        String idUser = "550e8400-e29b-41d4-a716-446655440001";
        String idChallenge = "550e8400-e29b-41d4-a716-446655440002";
        String idLanguage = "550e8400-e29b-41d4-a716-446655440003";
        String solutionText = "This is a test solution";

        UserSolutionDocument userSolutionDocument = new UserSolutionDocument();
        userSolutionDocument.setUserId(UUID.fromString(idUser));
        userSolutionDocument.setChallengeId(UUID.fromString(idChallenge));
        userSolutionDocument.setLanguageId(UUID.fromString(idLanguage));
        userSolutionDocument.setScore(13);

        when(userSolutionRepository.save(any(UserSolutionDocument.class)))
                .thenReturn(Mono.just(userSolutionDocument));
        when(userSolutionRepository.findByUserId(UUID.fromString(userSolutionDocument.getUserId().toString())))
                .thenReturn(Flux.empty());

        Mono<UserSolutionScoreDto> resultMono = userSolutionService.addSolution(idUser, idChallenge, idLanguage, solutionText);

        UUID userUuid = UUID.fromString(idUser);
        UUID challengeUuid = UUID.fromString(idChallenge);
        UUID languageUuid = UUID.fromString(idLanguage);

        StepVerifier.create(resultMono)
                .expectNextMatches(userSolutionScoreDto ->
                        userSolutionScoreDto.getUserId().equals(userUuid.toString())
                                && userSolutionScoreDto.getChallengeId().equals(challengeUuid.toString())
                                && userSolutionScoreDto.getLanguageId().equals(languageUuid.toString())
                                && userSolutionScoreDto.getSolutionText().equals(solutionText)
                                && userSolutionScoreDto.getScore() == 13)
                .verifyComplete();
    }
}


