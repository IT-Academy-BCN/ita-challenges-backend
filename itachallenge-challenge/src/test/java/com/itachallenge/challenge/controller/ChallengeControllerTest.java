package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.ReadUuidDto;
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

    private ChallengeDto challenge1;
    private ChallengeDto challenge2;
    private ChallengeDto challenge3;

    //private ReadUuidDto readUuidDto;

    @BeforeEach
    public void setUp(){

        //Set of Relateds for Challenge1
        ReadUuidDto readUuidDto1 = ReadUuidDto.builder()
                .uuid(UUID.fromString("7c55adc0-f985-4829-a2a6-e7571d1be7de"))
                .build();
        ReadUuidDto readUuidDto2 = ReadUuidDto.builder()
                .uuid(UUID.fromString("9818b618-b740-479d-8aac-962cd3e77cdd"))
                .build();
        Set<ReadUuidDto> readUuid1 = new HashSet<>();
        readUuid1.add(readUuidDto1);
        readUuid1.add(readUuidDto2);
        //Challenge 1
        UUID challengeUuid1 = UUID.fromString("468c70e7-57ea-4fba-b57d-73034ad5aa5f");
        challenge1 = ChallengeDto.builder()
                .uuid(challengeUuid1)
                .related(readUuid1)
                .build();


        //Set of Relateds for Challenge2
        ReadUuidDto readUuidDto3 = ReadUuidDto.builder()
                .uuid(UUID.fromString("468c70e7-57ea-4fba-b57d-73034ad5aa5f"))
                .build();
        ReadUuidDto readUuidDto4 = ReadUuidDto.builder()
                .uuid(UUID.fromString("9818b618-b740-479d-8aac-962cd3e77cdd"))
                .build();
        Set<ReadUuidDto> readUuid2 = new HashSet<>();
        readUuid1.add(readUuidDto3);
        readUuid1.add(readUuidDto4);
        //Challenge2
        UUID challengeUuid2 = UUID.fromString("7c55adc0-f985-4829-a2a6-e7571d1be7de");
        challenge2 = ChallengeDto.builder()
                .uuid(challengeUuid2)
                .related(readUuid2)
                .build();


        //Set of Relateds for Challenge3
        ReadUuidDto readUuidDto5 = ReadUuidDto.builder()
                .uuid(UUID.fromString("468c70e7-57ea-4fba-b57d-73034ad5aa5f"))
                .build();
        ReadUuidDto readUuidDto6 = ReadUuidDto.builder()
                .uuid(UUID.fromString("7c55adc0-f985-4829-a2a6-e7571d1be7de"))
                .build();
        Set<ReadUuidDto> readUuid3 = new HashSet<>();
        readUuid1.add(readUuidDto5);
        readUuid1.add(readUuidDto6);
        //Challenge3
        UUID challengeUuid3 = UUID.fromString("9818b618-b740-479d-8aac-962cd3e77cdd");
        challenge3 = ChallengeDto.builder()
                .uuid(challengeUuid3)
                .related(readUuid3)
                .build();
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

