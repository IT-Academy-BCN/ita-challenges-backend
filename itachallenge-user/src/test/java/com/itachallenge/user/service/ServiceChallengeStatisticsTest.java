package com.itachallenge.user.service;

import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ServiceChallengeStatisticsTest {

    @Test
    void getChallengeStatistics() {
        //region VARIABLES
        List<UUID> challengeIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        List<ChallengeStatisticsDto> challengeList;
        Mono<List<ChallengeStatisticsDto>> result;
        ServiceChallengeStatistics serviceChallengeStatistics = new ServiceChallengeStatistics();

        //endregion VARIABLES


        //region TESTS
        result = serviceChallengeStatistics.GetChallengeStatistics(challengeIds);
        challengeList = result.block();

        assertNotNull(challengeList);
        assertEquals(challengeIds.size(), challengeList.size());

        //endregion TESTS


        // OUT

    }
}