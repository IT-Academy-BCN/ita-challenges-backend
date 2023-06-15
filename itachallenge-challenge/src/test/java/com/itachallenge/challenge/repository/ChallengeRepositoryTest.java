package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.documents.Challenge;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.when;

class ChallengeRepositoryTest {

    private final ChallengeRepository challengeRepository = Mockito.mock(ChallengeRepository.class);

    @Test
    void findAllByResourcesContaining_ShouldReturnMatchingChallenges() {
        //Existing Resource ID of challenge1
        UUID resourceId = UUID.fromString("3b6ac964-dc93-4c14-a4da-e20a977c4c4a");

        // Init Data
        Challenge challenge1 = Challenge.builder()
                .uuid(UUID.fromString("c99f27b9-1dfb-483a-aa2b-33021000b0c1"))
                .resources(Set.of(UUID.fromString("09dd7278-8be5-471a-b706-abda9150094f"), UUID.fromString("3b6ac964-dc93-4c14-a4da-e20a977c4c4a")))
                .build();

        Challenge challenge2 = Challenge.builder()
                .uuid(UUID.fromString("330a49d1-84cb-4e89-adf3-5e439aeb3c41"))
                .resources(Set.of(UUID.fromString("3a9a92b9-4e0e-4fda-b4c6-b6d3de0e8e3c"), UUID.fromString("0a67c417-03ab-4ad2-8989-7c764bdf2230")))
                .build();


        when(challengeRepository.findAllByResourcesContaining(resourceId))
                .thenReturn(Flux.just(challenge1));

        Flux<Challenge> result = challengeRepository.findAllByResourcesContaining(resourceId);

        StepVerifier.create(result)
                .expectNext(challenge1)
                .verifyComplete();
    }
}
