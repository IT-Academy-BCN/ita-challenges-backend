package com.itachallenge.user.controller;

import com.itachallenge.user.dto.UserSolutionRequestDto;
import com.itachallenge.user.dto.UserSolutionResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class UserControllerSpringTest {
    @Autowired
    private WebTestClient webTestClient;

    String uri = "/itachallenge/api/v1/user/solution";
    String solution = "This is the submitted solution";
    String userId = UUID.randomUUID().toString();
    String challengeId = UUID.randomUUID().toString();
    String languageId = UUID.randomUUID().toString();

    @Test
    void addSolution_WithCorrectParameters_ExpectsReturn200Test() {

        UserSolutionRequestDto userSolutionRequestDto = new UserSolutionRequestDto(
                userId, challengeId, languageId, "ENDED", solution);
        UserSolutionResponseDto userSolutionResponseDto = new UserSolutionResponseDto(
                userId, challengeId, languageId, solution);

        webTestClient.put()
                .uri(uri)
                .bodyValue(userSolutionRequestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(response ->{
                    Assertions.assertNotNull(response.getResponseBody());
                    assert response.getResponseBody().containsKey("uuid_user");
                    assert response.getResponseBody().containsKey("uuid_challenge");
                    assert response.getResponseBody().containsKey("uuid_language");

                    assert response.getResponseBody().get("uuid_language").equals(userSolutionResponseDto.getLanguageId());
                    assert response.getResponseBody().get("uuid_challenge").equals(userSolutionResponseDto.getChallengeId());
                    assert response.getResponseBody().get("uuid_user").equals(userSolutionResponseDto.getUserId());
                    assert response.getResponseBody().get("solution_text").equals(userSolutionResponseDto.getSolutionText());
                });
    }

    @Test
    void addSolution_WithEmptySolution_ReturnBadRequestError_Test() {

        UserSolutionRequestDto userSolutionRequestDto1 = new UserSolutionRequestDto(
                userId, challengeId, languageId, "ENDED", "");
        UserSolutionRequestDto userSolutionRequestDto2 = new UserSolutionRequestDto(
                userId, challengeId, languageId, "ENDED", null);
        UserSolutionRequestDto userSolutionRequestDto3 = new UserSolutionRequestDto(
                userId, challengeId, languageId, "ENDED", " ");
        List<UserSolutionRequestDto> userSolutionRequestDtos = List.of(userSolutionRequestDto1, userSolutionRequestDto2, userSolutionRequestDto3);

        for(UserSolutionRequestDto userSolutionRequestDto : userSolutionRequestDtos){
            webTestClient.put()
                    .uri(uri)
                    .bodyValue(userSolutionRequestDto)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
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
