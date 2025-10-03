package com.itachallenge.user.controller;

import com.itachallenge.user.dto.SubmitSolutionResponseDto;
import com.itachallenge.user.dto.UserSolutionRequestDto;
import com.itachallenge.user.service.ExternalGithubService;
import com.itachallenge.user.service.IUserSolutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class UserControllerSpringTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IUserSolutionService userSolutionService;

    @MockBean
    private ExternalGithubService externalGithubService;

    private String uri;
    private String userId;
    private String challengeId;
    private String languageId;
    private String solution;

    @BeforeEach
    void setUp() {
        uri = "/itachallenge/api/v1/user/solution";
        userId = "e20e8efe-f38f-4fdd-89b5-201da705b853";
        challengeId = "7f7e1c41-b122-4e8e-9778-86fd82734666";
        languageId = "f87bf12f-e8ea-4b8c-8bb6-12c02756c765";
        solution = "This is the submitted solution";
    }

    @Test
    void testAddSolution() {
        UserSolutionRequestDto requestDto = UserSolutionRequestDto.builder()
                .userId(userId)
                .challengeId(challengeId)
                .languageId(languageId)
                .status("SUBMITTED_COMPLETE")
                .solutionText(solution)
                .build();

        SubmitSolutionResponseDto responseDto = SubmitSolutionResponseDto.builder()
                .solutionText(solution)
                .isSolved(true)
                .timesSolved(42)
                .build();

        when(userSolutionService.addSolution(org.mockito.ArgumentMatchers.any(UserSolutionRequestDto.class)))
                .thenReturn(Mono.just(responseDto));

        webTestClient.put()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SubmitSolutionResponseDto.class)
                .value(res -> {
                    assertEquals(solution, res.getSolutionText());
                    assertTrue(res.getIsSolved());
                    assertEquals(42, res.getTimesSolved());
                });
    }
}
