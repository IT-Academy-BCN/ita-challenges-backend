package com.itachallenge.user.service;

import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServiceChallengeStatisticsTest {

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
}