package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import com.itachallenge.user.enums.ChallengeStatus;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
@SpringBootTest
class ServiceChallengeStatisticsTest {


    @Mock
    private IUserSolutionRepository userSolutionRepository;

    @InjectMocks
    ServiceChallengeStatistics statisticsService;

    @Test
    void getChallengeStatistics() {
        //region VARIABLES
        List<UUID> challengeIds;
        List<ChallengeStatisticsDto> challengeList;
        Mono<List<ChallengeStatisticsDto>> result;
        ServiceChallengeStatistics serviceChallengeStatistics = new ServiceChallengeStatistics();

        //endregion VARIABLES


        //region TEST INITIALIZATION
        challengeIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        //endregion TEST INITIALIZATION


        //region TESTS
        result = serviceChallengeStatistics.getChallengeStatistics(challengeIds);
        challengeList = result.block();

        assertNotNull(challengeList);
        assertEquals(challengeIds.size(), challengeList.size());

        //endregion TESTS

    }

    @DisplayName("Should return number of BookmarkedTrue by idChallenge")
    @Test
    void testGetBookmarkCountByIdChallenge(){
        UUID idChallenge = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
        boolean isBookmarked = true;
        long expectedValue = 2L;

        UserSolutionDocument userSolutionDocument = new UserSolutionDocument();
        UserSolutionDocument userSolutionDocument2 = new UserSolutionDocument();
        userSolutionDocument.setChallengeId(idChallenge);
        userSolutionDocument2.setChallengeId(idChallenge);
        userSolutionDocument.setBookmarked(true);
        userSolutionDocument2.setBookmarked(true);

        when(userSolutionRepository.countByChallengeIdAndBookmarked(idChallenge, isBookmarked))
                .thenReturn(Mono.just(2L));

        Mono<Long> resultMono = statisticsService.getBookmarkCountByIdChallenge(idChallenge);

        StepVerifier.create(resultMono)
                .expectNext(expectedValue)
                .verifyComplete();

    }

    @Test
    void getChallengeUsersPercentageTest() {
        List<SolutionDocument> solutionField = Arrays.asList(new SolutionDocument(UUID.randomUUID(), "solution1Text"));
        UUID challengeId = UUID.randomUUID();
        List<UserSolutionDocument> userSolutions = Arrays.asList(
                new UserSolutionDocument(UUID.randomUUID(), UUID.randomUUID(), challengeId, UUID.randomUUID(), false, ChallengeStatus.ENDED, 45, solutionField),
                new UserSolutionDocument(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false, ChallengeStatus.ENDED, 75, solutionField)
        );

        when(userSolutionRepository.findAll()).thenReturn(Flux.fromIterable(userSolutions));

        Mono<Float> result = statisticsService.getChallengeUsersPercentage(challengeId);
        StepVerifier.create(result)
                .expectNextMatches(percentage -> percentage >= 0)
                .verifyComplete();
    }
}