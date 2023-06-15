package com.itachallenge.challenge.service;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.documents.Detail;
import com.itachallenge.challenge.documents.Example;
import com.itachallenge.challenge.repository.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;




class ChallengeServiceTest {

    @Mock
    ChallengeRepository challengeRepository;

    @InjectMocks
    ChallengeService challengeService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void removeResourcesById_Successfull() {
        //Existing ID
        UUID resourceId = UUID.fromString("69814d46-dd12-4e22-8e1e-2cdaf31dca03");

        // Init Data
        Challenge challenge1 = Challenge.builder()
                .uuid(resourceId)
                .resources(Set.of(UUID.fromString("09dd7278-8be5-471a-b706-abda9150094f"), UUID.fromString("3b6ac964-dc93-4c14-a4da-e20a977c4c4a")))
                .build();

        Challenge challenge2 = Challenge.builder()
                .uuid(UUID.fromString("330a49d1-84cb-4e89-adf3-5e439aeb3c41"))
                .resources(Set.of(UUID.fromString("3a9a92b9-4e0e-4fda-b4c6-b6d3de0e8e3c"), UUID.fromString("0a67c417-03ab-4ad2-8989-7c764bdf2230")))
                .build();


        when(challengeRepository.findAllByResourcesContaining(resourceId)).thenReturn(Flux.just(challenge1));
        when(challengeRepository.save(eq(challenge1)))
                .thenReturn(Mono.just(challenge1));
        when(challengeRepository.save(eq(challenge2)))
                .thenReturn(Mono.just(challenge2));

        // Act
        boolean result = challengeService.removeResourcesById(resourceId);

        // Assert
        assertTrue(result);

        verify(challengeRepository, times(1)).save(challenge1);
        verify(challengeRepository, times(1)).findAllByResourcesContaining(resourceId);

        assertEquals(2, challenge1.getResources().size());
        assertFalse(challenge1.getResources().contains(resourceId));

        assertEquals(3, challenge2.getResources().size());
        assertFalse(challenge2.getResources().contains(resourceId));

    }

    @Test
    void removeResourcesById_NotSuccessfull(){
        //Non Existing ID
        UUID resourceId = UUID.randomUUID();

        Challenge challenge1 = Challenge.builder()
                .uuid(UUID.randomUUID())
                .resources(Set.of(UUID.fromString("09dd7278-8be5-471a-b706-abda9150094f"), UUID.fromString("3b6ac964-dc93-4c14-a4da-e20a977c4c4a")))
                .build();

        Challenge challenge2 = Challenge.builder()
                .uuid(UUID.fromString("330a49d1-84cb-4e89-adf3-5e439aeb3c41"))
                .resources(Set.of(UUID.fromString("3a9a92b9-4e0e-4fda-b4c6-b6d3de0e8e3c"), UUID.fromString("0a67c417-03ab-4ad2-8989-7c764bdf2230")))
                .build();

        when(challengeRepository.findAllByResourcesContaining(resourceId)).thenReturn(Flux.empty());
        when(challengeRepository.save(eq(challenge1)))
                .thenReturn(Mono.just(challenge1));
        when(challengeRepository.save(eq(challenge2)))
                .thenReturn(Mono.just(challenge2));

        // Act
        boolean result = challengeService.removeResourcesById(resourceId);

        // Assert
        assertFalse(result);

        verify(challengeRepository, times(0)).save(challenge1);
        verify(challengeRepository, times(1)).findAllByResourcesContaining(resourceId);

        assertEquals(3, challenge1.getResources().size());
        assertFalse(challenge1.getResources().contains(resourceId));

        assertEquals(3, challenge2.getResources().size());
        assertFalse(challenge2.getResources().contains(resourceId));
    }
}