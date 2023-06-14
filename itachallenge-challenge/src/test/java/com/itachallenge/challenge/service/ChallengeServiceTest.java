package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.Challenge;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.helpers.ChallengeMapper;
import com.itachallenge.challenge.repository.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = ChallengeService.class)
public class ChallengeServiceTest {

    @Autowired
    private WebTestClient webTestClient;

    //@Autowired
    //GenericResultDto<ChallengeDto> genericResultDto;
    @InjectMocks
    private ChallengeService challengeService;
    @MockBean
    private ChallengeRepository challengeRepository;
    @MockBean
    private ChallengeMapper challengeMapper;

    //private List<Challenge> listOfChallenges;
    private ChallengeDto challenge1;
    private ChallengeDto challenge2;
    private ChallengeDto challenge3;
    private Challenge challenge4;

    @BeforeEach
    public void setUp(){

        MockitoAnnotations.openMocks(this);
        //listOfChallenges = new ArrayList<>();

        //Set of Relateds for Challenge1
        Set<String> relateds1 = new HashSet<>();
        String id11 = "7c55adc0-f985-4829-a2a6-e7571d1be7de";
        String id12 = "9818b618-b740-479d-8aac-962cd3e77cdd";
        relateds1.add(id11);
        relateds1.add(id12);

        //Set of Relateds for Challenge2
        Set<String> relateds2 = new HashSet<>();
        String id21 = "468c70e7-57ea-4fba-b57d-73034ad5aa5f";
        String id22 = "9818b618-b740-479d-8aac-962cd3e77cdd";
        relateds2.add(id21);
        relateds2.add(id22);

        //Set of Relateds for Challenge3
        Set<String> relateds3 = new HashSet<>();
        String id31 = "468c70e7-57ea-4fba-b57d-73034ad5aa5f";
        String id32 = "7c55adc0-f985-4829-a2a6-e7571d1be7de";
        relateds3.add(id31);
        relateds3.add(id32);

        //Challenge 1
        String challengeUuid1 = "468c70e7-57ea-4fba-b57d-73034ad5aa5f";
        challenge1 = ChallengeDto.builder()
                .uuid(challengeUuid1)
                .title("Challenge 1")
                .relatedChallenges(relateds1)
                .build();

        //Challenge2
        String challengeUuid2 = "7c55adc0-f985-4829-a2a6-e7571d1be7de";
        challenge2 = ChallengeDto.builder()
                .uuid(challengeUuid2)
                .title("Challenge 2")
                .relatedChallenges(relateds2)
                .build();

        //Challenge3
        String challengeUuid3 = "9818b618-b740-479d-8aac-962cd3e77cdd";
        challenge3 = ChallengeDto.builder()
                .uuid(challengeUuid3)
                .title("Challenge 3")
                .relatedChallenges(relateds3)
                .build();

        //Challenge4
        String challengeUuid4 = "2a8e07dd-b7ea-4507-92a0-5f413d0367f1";
        challenge4 = Challenge.builder()
                .uuid(challengeUuid4)
                .title("Challenge 4")
                .relatedChallenges(relateds3)
                .build();

        //listOfChallenges.add(challenge1);
        //listOfChallenges.add(challenge2);
        //listOfChallenges.add(challenge3);

    }

    @Test
    void getRelatedsTest(){

        // Set up the behavior of the mock repository
        Mockito.when(challengeRepository.findByUuid("2a8e07dd-b7ea-4507-92a0-5f413d0367f1")).thenReturn(Mono.just(challenge4));
        // Invoke the method that depends on the findByUuid operation
        Mono<Challenge> resultMono = challengeRepository.findByUuid("2a8e07dd-b7ea-4507-92a0-5f413d0367f1");
        // Assert the result using Reactor Test's StepVerifier
        StepVerifier.create(resultMono)
                .expectNext(challenge4)
                .verifyComplete();

        //Flux of all relateds
        Flux<ChallengeDto> relatedsFlux = resultMono
                .map(challenge5 -> challenge5.getRelatedChallenges())
                .flatMapMany(Flux::fromIterable)
                .flatMap(idRelated -> challengeRepository.findByUuid(idRelated))
                .map(challengeMapper::mapToChallengeDto);

        List<ChallengeDto> relateds = relatedsFlux
                .toStream()
                .collect(Collectors.toList());

        // Create an instance of GenericResultDto
        GenericResultDto<ChallengeDto> expectedResult = new GenericResultDto<>();
        expectedResult.setInfo(0, -1, challenge1.getRelatedChallenges().size(), relateds);
        Mono<GenericResultDto<ChallengeDto>> relatedsToMono = Mono.just(expectedResult);

        Mono<GenericResultDto<ChallengeDto>> relatedsFromService =challengeService.getRelateds(challenge1.getUuid(),0,-1);

        StepVerifier.create(relatedsFromService)
                .expectSubscription()
                .expectNextMatches(related -> related.getOffset() == 0)
                .expectNextMatches(related -> related.getLimit() == -1)
                .expectNextMatches(related -> related.getCount() == 2)
                .expectNextMatches(related -> related.getResults().size() == 2)
                // .expectNextMatches(related -> related.getResults().get(0).getTitle().equals("Challenge 2"))
                .verifyComplete();
    }
}
