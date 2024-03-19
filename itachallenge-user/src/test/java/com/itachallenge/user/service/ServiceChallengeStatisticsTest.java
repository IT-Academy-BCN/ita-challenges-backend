package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServiceChallengeStatisticsTest {

    ServiceChallengeStatistics serviceChallengeStatistics = new ServiceChallengeStatistics();

    @Test
    void getChallengeStatistics() {
        //region VARIABLES
        List<UUID> challengeIds;
        List<ChallengeStatisticsDto> challengeList;
        Mono<List<ChallengeStatisticsDto>> result;

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

    @Test
    void getChallengeUserPercentageTest() {

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