package com.itachallenge.user.service;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceChallengeStatisticsTest {

    @InjectMocks
    ServiceChallengeStatistics serviceChallengeStatistics;

    @Mock
    IUserSolutionRepository userSolutionRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
//
//    @Test
//    void testGetChallengesStatistics() {
//        UUID challengeId1 = UUID.randomUUID();
//        UUID challengeId2 = UUID.randomUUID();
//        List<UUID> challengeIds = Arrays.asList(challengeId1, challengeId2);
//
//        UUID userSolutionId1 = UUID.randomUUID();
//        UUID userSolutionId2 = UUID.randomUUID();
//        UserSolutionDocument userSolution1 = UserSolutionDocument
//                .builder()
//                .uuid(userSolutionId1)
//                .challengeId(challengeId1)
//                .bookmarked(true)
//                .build();
//        UserSolutionDocument userSolution2 = UserSolutionDocument
//                .builder()
//                .uuid(userSolutionId2)
//                .challengeId(challengeId2)
//                .bookmarked(false)
//                .build();
//        List<UserSolutionDocument> userSolutions = Arrays.asList(userSolution1, userSolution2);
//
//        when(userSolutionRepository.findAll()).thenReturn(Flux.fromIterable(userSolutions));
//
//        List<ChallengeStatisticsDto> result = service.getChallengesStatistics(challengeIds).block();
//
//        assertNotNull(result);
//        assertEquals(2, result.size());
//
//        // Example assertion: Check if the result contains the expected ChallengeStatisticsDto
//        ChallengeStatisticsDto resultDto = result.get(0);
//        assertEquals(challengeId1, resultDto.getChallengeId());
//        // Add more assertions based on your implementation
//    }

    @Test
    void testGetChallengeUsersPercentage() {
        UUID challengeId = UUID.randomUUID();
        List<UserSolutionDocument> userSolutions = Arrays.asList(
                UserSolutionDocument.builder().uuid(UUID.randomUUID()).challengeId(challengeId).bookmarked(true).build(),
                UserSolutionDocument.builder().uuid(UUID.randomUUID()).challengeId(challengeId).bookmarked(false).build(),
                UserSolutionDocument.builder().uuid(UUID.randomUUID()).challengeId(UUID.randomUUID()).bookmarked(true).build()
        );

        when(userSolutionRepository.findAll()).thenReturn(Flux.fromIterable(userSolutions));

        Mono<Float> result = serviceChallengeStatistics.getChallengeUsersPercentage(challengeId);

        assertNotNull(result);
        assertEquals(66.67f, result.block(), 0.01f);  // 2 out of 3 user solutions have the specified challenge
    }

    @Test
    void testGetChallengePopularity() {
        UUID challengeId = UUID.randomUUID();
        List<UserSolutionDocument> userSolutions = Arrays.asList(
                UserSolutionDocument.builder().uuid(UUID.randomUUID()).challengeId(challengeId).bookmarked(true).build(),    // ok
                UserSolutionDocument.builder().uuid(UUID.randomUUID()).challengeId(challengeId).bookmarked(false).build(),   //
                UserSolutionDocument.builder().uuid(UUID.randomUUID()).challengeId(challengeId).bookmarked(true).build(),    // ok
                UserSolutionDocument.builder().uuid(UUID.randomUUID()).challengeId(UUID.randomUUID()).bookmarked(true).build()

        );

        when(userSolutionRepository.findAll()).thenReturn(Flux.fromIterable(userSolutions));

        Mono<Integer> result = serviceChallengeStatistics.getChallengePopularity(challengeId);

        assertNotNull(result);
        assertEquals(2, result.block());  // 2 out of 4 user solutions have the specified challenge and are bookmarked
    }

    @Test
    void testGetGlobalChallengesIds() {
        UUID challengeId1 = UUID.randomUUID();
        UUID challengeId2 = UUID.randomUUID();
        List<UserSolutionDocument> userSolutions = Arrays.asList(
                UserSolutionDocument.builder().uuid(UUID.randomUUID()).challengeId(challengeId1).bookmarked(true).build(),
                UserSolutionDocument.builder().uuid(UUID.randomUUID()).challengeId(challengeId2).bookmarked(false).build()
        );

        when(userSolutionRepository.findAll()).thenReturn(Flux.fromIterable(userSolutions));

        Mono<List<UUID>> result = serviceChallengeStatistics.getGlobalChallengesIds();

        assertNotNull(result);
        List<UUID> globalChallengeIds = result.block();
        assertEquals(2, globalChallengeIds.size());
        assertTrue(globalChallengeIds.contains(challengeId1));
        assertTrue(globalChallengeIds.contains(challengeId2));
    }

    @Test
    void getUserSolutionsChallenge() {
    }

    // Add more tests based on requirements
}

