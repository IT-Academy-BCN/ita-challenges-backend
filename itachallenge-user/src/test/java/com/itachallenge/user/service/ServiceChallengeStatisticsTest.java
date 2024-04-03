package com.itachallenge.user.service;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import com.itachallenge.user.repository.IUserSolutionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ServiceChallengeStatisticsTest {

    @Mock
    private IUserSolutionRepository userSolutionRepository;
    @InjectMocks
    ServiceChallengeStatistics serviceChallengeStatistics;

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
    void getChallengeUsersPercentageTest() {
        List<UserSolutionDocument> userSolutions = new ArrayList<>();
        userSolutions.add(new UserSolutionDocument(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false, "started", 0, new ArrayList<>()));
        userSolutions.add(new UserSolutionDocument(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false, "ended", 0, new ArrayList<>()));

        when(userSolutionRepository.findAll()).thenReturn(Flux.fromIterable(userSolutions));

        UUID challengeId = UUID.randomUUID();

        Mono<Float> result = serviceChallengeStatistics.getChallengeUsersPercentage(challengeId);
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }
}