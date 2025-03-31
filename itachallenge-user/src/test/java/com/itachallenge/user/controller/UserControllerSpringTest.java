package com.itachallenge.user.controller;

import com.itachallenge.user.dto.UserSolutionDto;
import com.itachallenge.user.dto.UserSolutionScoreDto;
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

        UserSolutionDto userSolutionDto = new UserSolutionDto(
                userId, challengeId, languageId, "ENDED", solution);
        UserSolutionScoreDto userSolutionScoreDto = new UserSolutionScoreDto(
                userId, challengeId, languageId, solution);

        webTestClient.put()
                .uri(uri)
                .bodyValue(userSolutionDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .consumeWith(response ->{
                    Assertions.assertNotNull(response.getResponseBody());
                    assert response.getResponseBody().containsKey("uuid_user");
                    assert response.getResponseBody().containsKey("uuid_challenge");
                    assert response.getResponseBody().containsKey("uuid_language");

                    assert response.getResponseBody().get("uuid_language").equals(userSolutionScoreDto.getLanguageId());
                    assert response.getResponseBody().get("uuid_challenge").equals(userSolutionScoreDto.getChallengeId());
                    assert response.getResponseBody().get("uuid_user").equals(userSolutionScoreDto.getUserId());
                    assert response.getResponseBody().get("solution_text").equals(userSolutionScoreDto.getSolutionText());
                });
    }

    @Test
    void addSolution_WithEmptySolution_ReturnBadRequestError_Test() {

        UserSolutionDto userSolutionDto1 = new UserSolutionDto(
                userId, challengeId, languageId, "ENDED", "");
        UserSolutionDto userSolutionDto2 = new UserSolutionDto(
                userId, challengeId, languageId, "ENDED", null);
        UserSolutionDto userSolutionDto3 = new UserSolutionDto(
                userId, challengeId, languageId, "ENDED", " ");
        List<UserSolutionDto> userSolutionDtos = List.of(userSolutionDto1, userSolutionDto2, userSolutionDto3);

        for(UserSolutionDto userSolutionDto : userSolutionDtos){
            webTestClient.put()
                    .uri(uri)
                    .bodyValue(userSolutionDto)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Test
    void addSolution_WithIncorrectParameters_ReturnBadRequestError_Test() {
        String badUserId = userId.substring(0, userId.length() - 1);
        String badChallengeId = challengeId.substring(0, challengeId.length() - 1);
        String badLanguageId = languageId.substring(0, languageId.length() - 1);

        UserSolutionDto userSolutionDto1 = new UserSolutionDto(badUserId, challengeId, languageId, "ENDED", solution);
        UserSolutionDto userSolutionDto2 = new UserSolutionDto(userId, badChallengeId, languageId, "ENDED", solution);
        UserSolutionDto userSolutionDto3 = new UserSolutionDto(userId, challengeId, badLanguageId, "ENDED", solution);
        List<UserSolutionDto> userSolutions = List.of(userSolutionDto1, userSolutionDto2, userSolutionDto3);

        for(UserSolutionDto userSolutionDto : userSolutions){
            webTestClient.put()
                    .uri(uri)
                    .bodyValue(userSolutionDto)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }
}
