package com.itachallenge.user.service;
import com.itachallenge.user.dtos.UserSolutionScoreDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;


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

        Mono<UserSolutionScoreDto> resultMono = userSolutionService.addSolution(idUser, idChallenge, idLanguage, solutionText);

        UUID userUuid = userSolutionService.convertToUUID(idUser);
        UUID challengeUuid = userSolutionService.convertToUUID(idChallenge);
        UUID languageUuid = userSolutionService.convertToUUID(idLanguage);

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

