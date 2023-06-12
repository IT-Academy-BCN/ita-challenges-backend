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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.test.util.AssertionErrors.assertEquals;

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
   // private Challenge challenge4;

    @BeforeEach
    public void setUp(){
        //listOfChallenges = new ArrayList<>();

        //Set of Relateds for Challenge1
        Set<UUID> relateds1 = new HashSet<>();
        UUID id11 = UUID.fromString("7c55adc0-f985-4829-a2a6-e7571d1be7de");
        UUID id12 = UUID.fromString("9818b618-b740-479d-8aac-962cd3e77cdd");
        relateds1.add(id11);
        relateds1.add(id12);

        //Set of Relateds for Challenge2
        Set<UUID> relateds2 = new HashSet<>();
        UUID id21 = UUID.fromString("468c70e7-57ea-4fba-b57d-73034ad5aa5f");
        UUID id22 = UUID.fromString("9818b618-b740-479d-8aac-962cd3e77cdd");
        relateds2.add(id21);
        relateds2.add(id22);

        //Set of Relateds for Challenge3
        Set<UUID> relateds3 = new HashSet<>();
        UUID id31 = UUID.fromString("468c70e7-57ea-4fba-b57d-73034ad5aa5f");
        UUID id32 = UUID.fromString("7c55adc0-f985-4829-a2a6-e7571d1be7de");
        relateds3.add(id31);
        relateds3.add(id32);

        //Challenge 1
        UUID challengeUuid1 = UUID.fromString("468c70e7-57ea-4fba-b57d-73034ad5aa5f");
        challenge1 = ChallengeDto.builder()
                .uuid(challengeUuid1)
                .title("Challenge 1")
                .related(relateds1)
                .build();

        //Challenge2
        UUID challengeUuid2 = UUID.fromString("7c55adc0-f985-4829-a2a6-e7571d1be7de");
        challenge2 = ChallengeDto.builder()
                .uuid(challengeUuid2)
                .title("Challenge 2")
                .related(relateds2)
                .build();

        //Challenge3
        UUID challengeUuid3 = UUID.fromString("9818b618-b740-479d-8aac-962cd3e77cdd");
        challenge3 = ChallengeDto.builder()
                .uuid(challengeUuid3)
                .title("Challenge 3")
                .related(relateds3)
                .build();

        //Challenge4
       /* UUID challengeUuid4 = UUID.fromString("2a8e07dd-b7ea-4507-92a0-5f413d0367f1");
        challenge4 = Challenge.builder()
                .uuid(challengeUuid4)
                .title("Challenge 4")
                .related(relateds3)
                .build();
*/
        //listOfChallenges.add(challenge1);
        //listOfChallenges.add(challenge2);
        //listOfChallenges.add(challenge3);

    }

    @Test
    void getRelatedsTest(){
        //En el repo me ha de retornar el challenge con los uuid de relacionados
        //Me ha de dar un Mono<SetUUID>
        //Lo paso a flux de UUID
        //recojo los challenges byId y los ingreso en un flux
        //Los cambio a Dto

        //Challenges relateds of challenge1
        List<ChallengeDto> relateds = new ArrayList<>();
        relateds.add(challenge2);
        relateds.add(challenge3);

        GenericResultDto<ChallengeDto> expectedResult = new GenericResultDto<>();
        expectedResult.setInfo(0, -1, challenge1.getRelated().size(), relateds);
        Mono<GenericResultDto<ChallengeDto>> relatedsToMono = Mono.just(expectedResult);

        Mono<GenericResultDto<ChallengeDto>> relatedsFromService =challengeService.getRelateds(challenge1.getUuid(),0,-1);

        StepVerifier.create(relatedsFromService)
                .expectSubscription()
                .expectNextMatches(related -> related.getOffset() == 0)
                .expectNextMatches(related -> related.getLimit() == -1)
                .expectNextMatches(related -> related.getCount() == 2)
                .expectNextMatches(related -> related.getResults().size() == 2)
                .expectNextMatches(related -> related.getResults().get(0).getTitle().equals("Challenge 2"))
                .verifyComplete();
    }



}
