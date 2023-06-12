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
        String resourceId = "69814d46-dd12-4e22-8e1e-2cdaf31dca03";

        //Data Init
        Challenge challenge1 = Challenge.builder()
                .resources(Set.of("69814d46-dd12-4e22-8e1e-2cdaf31dca03","1ac9c154-4031-4cb1-aabd-1f9e507dc847","16f33136-ae3f-4638-a8e2-95e5e0422174"))
                .build();

        Challenge challenge2 = Challenge.builder()
                .resources(Set.of("16f33136-ae3f-4638-a8e2-95e5e0422174", "7b9896be-d2c7-4b34-a336-7e642a7f09d7", "9ab1c090-8f49-4158-8261-89e4640c27e4"))
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
        String resourceId = "12345AWA-dd12-4e22-8e1e-2cdaf31dca03";

        //Data Init
        Challenge challenge1 = Challenge.builder()
                .resources(Set.of("69814d46-dd12-4e22-8e1e-2cdaf31dca03","1ac9c154-4031-4cb1-aabd-1f9e507dc847","16f33136-ae3f-4638-a8e2-95e5e0422174"))
                .build();

        Challenge challenge2 = Challenge.builder()
                .resources(Set.of("16f33136-ae3f-4638-a8e2-95e5e0422174", "7b9896be-d2c7-4b34-a336-7e642a7f09d7", "9ab1c090-8f49-4158-8261-89e4640c27e4"))
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