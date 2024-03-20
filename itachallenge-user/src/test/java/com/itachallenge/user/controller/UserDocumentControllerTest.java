package com.itachallenge.user.controller;

import com.itachallenge.user.dtos.UserSolutionDto;
import com.itachallenge.user.dtos.UserSolutionScoreDto;
import com.itachallenge.user.enums.ChallengeStatus;
import com.itachallenge.user.service.IUserSolutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("UserDocumentControllerTest - addSolution - create and return a new document with status 200 OK")
    @Test
    void addSolutionIfValidSolutionThenSolutionAdded_test() {
        String URI_TEST = "/solution";
        UserSolutionDto userSolutionDto = new UserSolutionDto();
        userSolutionDto.setUserId("550e8400-e29b-41d4-a716-446655440001");
        userSolutionDto.setChallengeId("550e8400-e29b-41d4-a716-446655440002");
        userSolutionDto.setLanguageId("550e8400-e29b-41d4-a716-446655440003");
        userSolutionDto.setSolutionText("This is a test solution");

        UserSolutionScoreDto expectedResponse = new UserSolutionScoreDto(userSolutionDto.getUserId(),
                userSolutionDto.getChallengeId(), userSolutionDto.getLanguageId(),
                userSolutionDto.getSolutionText(), 13);

        when(userSolutionService.addSolution(userSolutionDto))
                .thenReturn(Mono.just(expectedResponse));

        webTestClient.put()
                .uri(CONTROLLER_URL + URI_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userSolutionDto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(UserSolutionScoreDto.class)
                .value(dto -> {
                    assert dto != null;
                    assert dto.getUserId() != null;
                    assert dto.getChallengeId() != null;
                    assert dto.getLanguageId() != null;
                    assert dto.getScore() >= 0;

                    verify(userSolutionService).addSolution(userSolutionDto);
                });
    }
    @DisplayName("UserDocumentControllerTest - addSolution - return 400 BAD REQUEST and don't save if dto is invalid")
    @Test
    void addSolutionIfInvalidValuesThenBadRequest_test() {
        String URI_TEST = "/solution";

        List<UserSolutionDto> testCases = Arrays.asList(
                new UserSolutionDto("invalid_uuid", "550e8400-e29b-41d4-a716-446655440002", "550e8400-e29b-41d4-a716-446655440003", null, "This is a test solution"),
                new UserSolutionDto("550e8400-e29b-41d4-a716-446655440001", "invalid_uuid", "550e8400-e29b-41d4-a716-446655440003", null, "This is a test solution"),
                new UserSolutionDto("550e8400-e29b-41d4-a716-446655440001", "550e8400-e29b-41d4-a716-446655440002", "invalid_uuid", null, "This is a test solution"),
                new UserSolutionDto("550e8400-e29b-41d4-a716-446655440001", "550e8400-e29b-41d4-a716-446655440002", "550e8400-e29b-41d4-a716-446655440003", null, ""),
                new UserSolutionDto()
        );

        for (UserSolutionDto testCase : testCases) {
            webTestClient.put()
                    .uri(CONTROLLER_URL + URI_TEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(testCase)
                    .exchange()
                    .expectStatus().isBadRequest();

            verifyNoInteractions(userSolutionService);
        }
    }
    @DisplayName("UserDocumentControllerTest - addSolution - return 500 Internal Server Error if Service returns runtime exception")
    @Test
    void addSolutionServiceThrowsExceptionInternalServerError_test() {
        String URI_TEST = "/solution";
        UserSolutionDto userSolutionDto = new UserSolutionDto();
        userSolutionDto.setUserId("550e8400-e29b-41d4-a716-446655440001");
        userSolutionDto.setChallengeId("550e8400-e29b-41d4-a716-446655440002");
        userSolutionDto.setLanguageId("550e8400-e29b-41d4-a716-446655440003");
        userSolutionDto.setStatus("STARTED");
        userSolutionDto.setSolutionText("This is a test solution");

        when(userSolutionService.addSolution(userSolutionDto))
                .thenReturn(Mono.error(new RuntimeException("Invalid challenge status: status was already ENDED")));

        webTestClient.put()
                .uri(CONTROLLER_URL + URI_TEST)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userSolutionDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
