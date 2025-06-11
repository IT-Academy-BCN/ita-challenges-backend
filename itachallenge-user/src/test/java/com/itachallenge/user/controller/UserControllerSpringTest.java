package com.itachallenge.user.controller;

import com.itachallenge.user.dto.UserSolutionRequestDto;
import com.itachallenge.user.dto.UserSolutionResponseDto;
import com.itachallenge.user.service.IUserSolutionService;
import com.itachallenge.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class UserControllerSpringTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @MockBean
    private IUserSolutionService userSolutionService;

    String uri = "/itachallenge/api/v1/user/solution";
    String solution = "This is the submitted solution";
    String userId = UUID.randomUUID().toString();
    String challengeId = UUID.randomUUID().toString();
    String languageId = UUID.randomUUID().toString();

    @Test
    void testAddSolution() {
        UserSolutionRequestDto requestDto = UserSolutionRequestDto.builder()
                .userId(userId)
                .challengeId(challengeId)
                .languageId(languageId)
                .status("ENDED")
                .solutionText(solution)
                .build();

        UserSolutionResponseDto responseDto = UserSolutionResponseDto.builder()
                .userId(userId)
                .challengeId(challengeId)
                .languageId(languageId)
                .solutionText(solution)
                .build();

        Mockito.when(userSolutionService.addSolution(Mockito.any(UserSolutionRequestDto.class)))
                .thenReturn(Mono.just(responseDto));

        webTestClient.put()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserSolutionResponseDto.class)
                .value(res -> {
                    assert res.getUserId().equals(userId);
                    assert res.getChallengeId().equals(challengeId);
                    assert res.getLanguageId().equals(languageId);
                    assert res.getSolutionText().equals(solution);
                });
    }

    @Test
    void addSolution_WithIncorrectParameters_ReturnBadRequestError_Test() {
        String badUserId = userId.substring(0, userId.length() - 1);
        String badChallengeId = challengeId.substring(0, challengeId.length() - 1);
        String badLanguageId = languageId.substring(0, languageId.length() - 1);

        UserSolutionRequestDto userSolutionRequestDto1 = new UserSolutionRequestDto(badUserId, challengeId, languageId, "ENDED", solution);
        UserSolutionRequestDto userSolutionRequestDto2 = new UserSolutionRequestDto(userId, badChallengeId, languageId, "ENDED", solution);
        UserSolutionRequestDto userSolutionRequestDto3 = new UserSolutionRequestDto(userId, challengeId, badLanguageId, "ENDED", solution);
        List<UserSolutionRequestDto> userSolutions = List.of(userSolutionRequestDto1, userSolutionRequestDto2, userSolutionRequestDto3);

        for(UserSolutionRequestDto userSolutionRequestDto : userSolutions){
            webTestClient.put()
                    .uri(uri)
                    .bodyValue(userSolutionRequestDto)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }
}