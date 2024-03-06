package com.itachallenge.user.service;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
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
    void getChallengeUserPercentageTest() {
        ServiceChallengeStatistics serviceChallengeStatistics = new ServiceChallengeStatistics(); //TODO Temporary fix
        List<UUID> challengeIds = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        List<UserSolutionDocument> userSolutionDocuments = new ArrayList<>();
        userSolutionDocuments.add(UserSolutionDocument.builder()
                .challengeId(challengeIds.get(0))
                .userId(userIds.get(0))
                .status("started")
                .bookmarked(true)
                .languageId(UUID.randomUUID())
                .build());
        userSolutionDocuments.add(UserSolutionDocument.builder()
                .challengeId(challengeIds.get(1))
                .userId(userIds.get(1))
                .status("started")
                .bookmarked(true)
                .languageId(UUID.randomUUID())
                .build());
        userSolutionDocuments.add(UserSolutionDocument.builder()
                .challengeId(challengeIds.get(2))
                .userId(userIds.get(2))
                .status("ended")
                .bookmarked(false)
                .languageId(UUID.randomUUID())
                .solutionDocument(List.of(new SolutionDocument()))
                .build());
        userSolutionDocuments.add(UserSolutionDocument.builder()
                .challengeId(challengeIds.get(3))
                .userId(userIds.get(3))
                .status("empty")
                .bookmarked(true)
                .languageId(UUID.randomUUID())
                .build());

        assertNotNull(userSolutionDocuments);
        assertEquals(4, userSolutionDocuments.size());

        UUID challengeId = userSolutionDocuments.get(2).getChallengeId();
        List<UserSolutionDocument> userSolutionDocumentsChallenge = serviceChallengeStatistics.getUserSolutionsChallenge(userSolutionDocuments, challengeId);

        assertEquals(1, userSolutionDocumentsChallenge.size());

        float percentage = (float) userSolutionDocumentsChallenge.size()*100 / userSolutionDocuments.size();
        assertEquals(25.00, percentage, 0.01);
    }
}