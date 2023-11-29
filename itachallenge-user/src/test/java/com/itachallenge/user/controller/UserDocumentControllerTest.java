package com.itachallenge.user.controller;
import com.itachallenge.user.dtos.UserSolutionDto;
import com.itachallenge.user.dtos.UserSolutionScoreDto;
import com.itachallenge.user.service.UserSolutionServiceImp;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserDocumentControllerTest {

        @InjectMocks
        private UserController userController;

        @Mock
        private UserSolutionServiceImp userSolutionServiceImp;

        private WebTestClient webTestClient;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        public void testAddSolution() {

            UserSolutionDto userSolutionDto = new UserSolutionDto();
            userSolutionDto.setUserId("550e8400-e29b-41d4-a716-446655440001");
            userSolutionDto.setChallengeId("550e8400-e29b-41d4-a716-446655440002");
            userSolutionDto.setLanguageId("550e8400-e29b-41d4-a716-446655440003");
            userSolutionDto.setSolutionText("This is a test solution");

            UserSolutionScoreDto expectedResponse = new UserSolutionScoreDto(userSolutionDto.getUserId(),
                    userSolutionDto.getChallengeId(),userSolutionDto.getLanguageId(),
                    userSolutionDto.getSolutionText(),13);
            when(userSolutionServiceImp.addSolution(any(), any(), any(), any()))
                    .thenReturn(Mono.just(expectedResponse));

            Mono<ResponseEntity<UserSolutionScoreDto>> resultMono = userController.addSolution(userSolutionDto);

            StepVerifier.create(resultMono)
                    .expectNextMatches(responseEntity ->
                            responseEntity.getStatusCode() == HttpStatus.ACCEPTED
                                    && responseEntity.getBody().equals(expectedResponse))
                    .verifyComplete();

            verify(userSolutionServiceImp, times(1)).addSolution(
                    eq(userSolutionDto.getUserId()),
                    eq(userSolutionDto.getChallengeId()),
                    eq(userSolutionDto.getLanguageId()),
                    eq(userSolutionDto.getSolutionText())
            );
        }
    @Test
    public void testAddSolutionII(){

        UserSolutionDto userSolutionDto = new UserSolutionDto();
        userSolutionDto.setUserId("550e8400-e29b-41d4-a716-446655440001");
        userSolutionDto.setChallengeId("550e8400-e29b-41d4-a716-446655440002");
        userSolutionDto.setLanguageId("550e8400-e29b-41d4-a716-446655440003");
        userSolutionDto.setSolutionText("This is a test solution");

        UserSolutionScoreDto expectedResponse = new UserSolutionScoreDto(userSolutionDto.getUserId(),
                userSolutionDto.getChallengeId(),userSolutionDto.getLanguageId(),
                userSolutionDto.getSolutionText(),13);
        when(userSolutionServiceImp.addSolution(any(), any(), any(), any()))
                .thenReturn(Mono.just(expectedResponse));

        webTestClient.put()
                .uri("/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userSolutionDto)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(UserSolutionScoreDto.class)
                .value(dto -> {
                    assert dto != null;
                    assert dto.getUserId() != null;
                    assert dto.getChallengeId() != null;
                    assert dto.getLanguageId() != null;
                    assert dto.getScore() >= 0;

                    verify(userSolutionServiceImp, times(1)).addSolution(
                            userSolutionDto.getUserId(),
                            userSolutionDto.getChallengeId(),
                            userSolutionDto.getLanguageId(),
                            userSolutionDto.getSolutionText()
                    );

                });
    }
}


