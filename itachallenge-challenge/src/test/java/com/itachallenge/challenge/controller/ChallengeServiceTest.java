package com.itachallenge.challenge.controller;


import com.itachallenge.challenge.helper.ResourceHelper;
import com.itachallenge.challenge.service.ChallengeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ChallengeServiceTest {


    @Autowired
    ChallengeService challengeService;

    private static ResourceHelper resourceHelper;

    @DisplayName("Get All Challenges Test with StepVerifier")
    @Test
    void getAllChallengesTestWithStepVerifier() throws IOException {

        Mono<String> result = challengeService.getAllChallenges();

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }


    @DisplayName("Get All Challenges and ResourceHelper Test")
    @Test
    void getAllChallengesAndResourceHelperTest() throws IOException {

        resourceHelper = mock(ResourceHelper.class);
        String jsonFile = "C:\\Users\\Alfonso\\ita-challenges-backend\\itachallenge-challenge\\src\\test\\resources\\data-challenge.json";

        byte[] jsonData = Files.readAllBytes(Paths.get(jsonFile));
        String expected = new String(jsonData, "UTF-8");
        when(resourceHelper.readResourceAsString(jsonFile)).thenReturn(expected);

        Mono<String> result = challengeService.getAllChallenges();

        String actualJson = result.block();
        Assertions.assertEquals(expected, actualJson);


    }
}




