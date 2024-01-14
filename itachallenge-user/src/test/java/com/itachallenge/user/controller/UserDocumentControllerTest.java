package com.itachallenge.user.controller;

import com.itachallenge.user.dtos.UserSolutionDto;
import com.itachallenge.user.dtos.UserSolutionScoreDto;
import com.itachallenge.user.enums.ChallengeStatus;
import com.itachallenge.user.service.IUserSolutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(SpringExtension.class)
class UserDocumentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String CONTROLLER_URL = "/itachallenge/api/v1/user";

    @MockBean
    IUserSolutionService userSolutionService;
    @BeforeEach
    public void setUp() {
    }

    @Test
    void testHello() {
        String URI_TEST = "/test";

        webTestClient.get()
                .uri(CONTROLLER_URL + URI_TEST)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(String.class)
                .value(String::toString, equalTo("Hello from ITA User!!!"));
    }

    @Test
    void addSolution_ValidSolution_SolutionAdded() {
        String URI_TEST = "/solution";
        UserSolutionDto userSolutionDto = new UserSolutionDto();
        userSolutionDto.setUserId("550e8400-e29b-41d4-a716-446655440001");
        userSolutionDto.setChallengeId("550e8400-e29b-41d4-a716-446655440002");
        userSolutionDto.setLanguageId("550e8400-e29b-41d4-a716-446655440003");
        userSolutionDto.setStatus("STARTED");
        userSolutionDto.setSolutionText("This is a test solution");

        UserSolutionScoreDto expectedResponse = new UserSolutionScoreDto(userSolutionDto.getUserId(),
                userSolutionDto.getChallengeId(), userSolutionDto.getLanguageId(),
                userSolutionDto.getSolutionText(), 13);

        when(userSolutionService.addSolution(userSolutionDto.getUserId(),
                        userSolutionDto.getChallengeId(),
                        userSolutionDto.getLanguageId(),
                        userSolutionDto.getStatus(),
                        userSolutionDto.getSolutionText()))
                .thenReturn(Mono.just(expectedResponse));

        webTestClient.put()
                .uri(CONTROLLER_URL + URI_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userSolutionDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.ACCEPTED)
                .expectBody(UserSolutionScoreDto.class)
                .value(dto -> {
                    assert dto != null;
                    assert dto.getUserId() != null;
                    assert dto.getChallengeId() != null;
                    assert dto.getLanguageId() != null;
                    assert dto.getScore() >= 0;

                    verify(userSolutionService, times(1)).addSolution(
                            userSolutionDto.getUserId(),
                            userSolutionDto.getChallengeId(),
                            userSolutionDto.getLanguageId(),
                            userSolutionDto.getStatus(),
                            userSolutionDto.getSolutionText()
                    );
                });
    }

    @Test
    void addSolution_InvalidValues_BadRequest() {
        String URI_TEST = "/solution";

        List<UserSolutionDto> testCases = Arrays.asList(
                new UserSolutionDto("invalid_uuid", "550e8400-e29b-41d4-a716-446655440002", "550e8400-e29b-41d4-a716-446655440003", "STARTED", "This is a test solution"),
                new UserSolutionDto("550e8400-e29b-41d4-a716-446655440001", "invalid_uuid", "550e8400-e29b-41d4-a716-446655440003", "STARTED", "This is a test solution"),
                new UserSolutionDto("550e8400-e29b-41d4-a716-446655440001", "550e8400-e29b-41d4-a716-446655440002", "invalid_uuid", "STARTED", "This is a test solution"),
                new UserSolutionDto("550e8400-e29b-41d4-a716-446655440001", "550e8400-e29b-41d4-a716-446655440002", "550e8400-e29b-41d4-a716-446655440003", "", "This is a test solution"),
                new UserSolutionDto("550e8400-e29b-41d4-a716-446655440001", "550e8400-e29b-41d4-a716-446655440002", "550e8400-e29b-41d4-a716-446655440003", "STARTED", "")

        );

        for (UserSolutionDto testCase : testCases) {
            webTestClient.put()
                    .uri(CONTROLLER_URL + URI_TEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(testCase)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Test
    void addSolution_EmptyRequestBody_BadRequest() {
        String URI_TEST = "/solution";
        UserSolutionDto userSolutionDto = new UserSolutionDto();

        webTestClient.put()
                .uri(CONTROLLER_URL + URI_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userSolutionDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addSolution_ServiceThrowsException_InternalServerError() {
        String URI_TEST = "/solution";
        UserSolutionDto userSolutionDto = new UserSolutionDto();
        userSolutionDto.setUserId("550e8400-e29b-41d4-a716-446655440001");
        userSolutionDto.setChallengeId("550e8400-e29b-41d4-a716-446655440002");
        userSolutionDto.setLanguageId("550e8400-e29b-41d4-a716-446655440003");
        userSolutionDto.setStatus("STARTED");
        userSolutionDto.setSolutionText("This is a test solution");

        when(userSolutionService.addSolution(any(), any(), any(),any(), any()))
                .thenReturn(Mono.error(new RuntimeException("Test exception")));

        webTestClient.put()
                .uri(CONTROLLER_URL + URI_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userSolutionDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
