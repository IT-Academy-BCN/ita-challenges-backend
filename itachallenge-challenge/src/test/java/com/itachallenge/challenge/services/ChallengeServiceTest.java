package com.itachallenge.challenge.services;

import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.service.ChallengeService;
import com.itachallenge.challenge.service.ChallengeServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static reactor.core.publisher.Mono.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class ChallengeServiceTest {

    @MockBean
    private ChallengeService challengeService;

    @MockBean
    private ChallengeRepository challengeRepository;

    private List<ChallengeDto> listOfChallenges;

    private ChallengeDto challenge1;
    private ChallengeDto challenge2;
    private ChallengeDto challenge3;

    @BeforeEach
    public void setUp(){
        listOfChallenges = new ArrayList<>();

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
                .relateds(relateds1)
                .build();

        //Challenge2
        UUID challengeUuid2 = UUID.fromString("7c55adc0-f985-4829-a2a6-e7571d1be7de");
        challenge2 = ChallengeDto.builder()
                .uuid(challengeUuid2)
                .title("Challenge 2")
                .relateds(relateds2)
                .build();

        //Challenge3
        UUID challengeUuid3 = UUID.fromString("9818b618-b740-479d-8aac-962cd3e77cdd");
        challenge3 = ChallengeDto.builder()
                .uuid(challengeUuid3)
                .title("Challenge 3")
                .relateds(relateds3)
                .build();

        listOfChallenges.add(challenge1);
        listOfChallenges.add(challenge2);
        listOfChallenges.add(challenge3);
    }
    @Test
    public void getRelatedsTest(){

        //List of challenges relateds with challenge1
        List<ChallengeDto> relateds = new ArrayList<>();
        relateds.add(challenge2);
        relateds.add(challenge3);

        GenericResultDto<ChallengeDto> genericResultDto = new GenericResultDto<>();
        genericResultDto.setInfo(0,-1,2,relateds);
        Mono<GenericResultDto<ChallengeDto>> resultDtoMono= Mono.just(genericResultDto);

        given(challengeService.getRelateds(challenge1.getUuid(),0,-1)).willReturn(resultDtoMono);

        assertEquals(genericResultDto.getCount(),2);

    }


}
