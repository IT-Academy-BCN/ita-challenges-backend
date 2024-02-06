package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServiceChallengeStatisticsTest {


    @InjectMocks
    ServiceChallengeStatistics statisticsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getChallengeStatistics() {

        List<UUID> challengeIds;
        List<ChallengeStatisticsDto> challengeList;
        Mono<List<ChallengeStatisticsDto>> result;

        challengeIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        result = statisticsService.getChallengeStatistics(challengeIds);
        challengeList = result.block();

        assertNotNull(challengeList);
        assertEquals(challengeIds.size(), challengeList.size());

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
        List<UserSolutionDocument> userSolutionDocumentsChallenge = statisticsService.getUserSolutionsChallenge(userSolutionDocuments, challengeId);

        assertEquals(1, userSolutionDocumentsChallenge.size());

        float percentage = (float) userSolutionDocumentsChallenge.size()*100 / userSolutionDocuments.size();
        assertEquals(25.00, percentage, 0.01);
    }
}