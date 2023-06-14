package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.service.ChallengeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WebFluxTest(controllers = ChallengeController.class)
@AutoConfigureWebTestClient
public class ChallengeControllerTest {
    //region ATTRIBUTES
    @Autowired
    private WebTestClient webTestClient;
    private final String CHALLENGE_BASE_URL = "/itachallenge/api/v1/challenge";

    @MockBean
    private ChallengeService challengeService;
    @MockBean
    private ChallengeController challengeController;

    private List<ChallengeDto> listOfChallenges;
    private ChallengeDto challenge1;
    private ChallengeDto challenge2;
    private ChallengeDto challenge3;

    @BeforeEach
    public void setUp(){
        listOfChallenges = new ArrayList<>();

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
                .relatedChallenges(relateds1)
                .build();

        //Challenge2
        String challengeUuid2 = "7c55adc0-f985-4829-a2a6-e7571d1be7de";
        challenge2 = ChallengeDto.builder()
                .uuid(challengeUuid2)
                .relatedChallenges(relateds2)
                .build();

        //Challenge3
        String challengeUuid3 = "9818b618-b740-479d-8aac-962cd3e77cdd";
        challenge3 = ChallengeDto.builder()
                .uuid(challengeUuid3)
                .relatedChallenges(relateds3)
                .build();

        listOfChallenges.add(challenge1);
        listOfChallenges.add(challenge2);
        listOfChallenges.add(challenge3);

    }

    @Test
    @DisplayName("Test EndPoint: test")
    void TestEndPoint_test(){
        final String URI_TEST = "/test";
        webTestClient.get()
                .uri(CHALLENGE_BASE_URL + URI_TEST)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(s -> s, equalTo("Hello from ITA Challenge!!!"));
    }

 /*   @Test
    public void findAllRelatedsByUuidTest(){

        List<ChallengeDto> relateds = new ArrayList<>();

        //Relateds from challenge1
        relateds.add(challenge2);
        relateds.add(challenge3);

        Flux<ChallengeDto> relatedsToFlux = Flux.fromIterable(relateds);

        given(challengeService.getAllRelatedsByUuid(challenge1.getUuid())).willReturn(relatedsToFlux);

        webTestClient.get()
                .uri(CHALLENGE_BASE_URL + "/{id}/related", Collections.singletonMap("id", challenge1.getUuid()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ChallengeDto.class)
                .consumeWith(System.out::println);

    }*/

    @Test
    public void findRelatedsPaginatedTest(){
        List<ChallengeDto> relateds = new ArrayList<>();

        //Relateds from challenge1
        relateds.add(challenge2);
        relateds.add(challenge3);

        GenericResultDto<ChallengeDto> result = new GenericResultDto<>();
        result.setInfo(0,-1,2,relateds);
        Mono<GenericResultDto<ChallengeDto>> relatedsToMono = Mono.just(result);

        given(challengeService.getRelateds(challenge1.getUuid(),0,-1)).willReturn(relatedsToMono);

        webTestClient.get()
                .uri(CHALLENGE_BASE_URL + "/{id}/related", Collections.singletonMap("id", challenge1.getUuid()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(ChallengeDto.class)
                .consumeWith(System.out::println);
    }
}

