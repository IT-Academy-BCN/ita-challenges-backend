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
import org.springframework.http.ResponseEntity;
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

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
        }

        @Test
        void testAddSolution() {

            UserSolutionDto userSolutionDto = new UserSolutionDto();
            userSolutionDto.setUserId("550e8400-e29b-41d4-a716-446655440001");
            userSolutionDto.setChallengeId("550e8400-e29b-41d4-a716-446655440002");
            userSolutionDto.setLanguageId("550e8400-e29b-41d4-a716-446655440003");
            userSolutionDto.setSolutionText("This is a test solution");

            UserSolutionScoreDto expectedResponse = new UserSolutionScoreDto(/* ... valores esperados ... */);
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
    }

