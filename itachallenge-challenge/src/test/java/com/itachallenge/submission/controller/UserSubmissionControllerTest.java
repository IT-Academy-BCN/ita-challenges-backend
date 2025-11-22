package com.itachallenge.submission.controller;

import com.itachallenge.challenge.exception.NotFoundException;
import com.itachallenge.submission.exception.BadUUIDException;
import com.itachallenge.submission.exception.UserSubmissionGlobalExceptionHandler;
import com.itachallenge.submission.service.IUserSubmissionService;
import com.itachallenge.submission.dto.UserSubmissionResponseDto;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserSubmissionControllerTest {

    @Mock
    IUserSubmissionService userSubmissionService;

    @InjectMocks
    UserSubmissionController userSubmissionController;

    private WebTestClient webTestClient;

    private AutoCloseable mocks;


    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(userSubmissionController)
                .controllerAdvice(new UserSubmissionGlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }



    @Test
    void getAllSolutions_returnsSolutions() {
        String userId = UUID.randomUUID().toString();

        UserSubmissionResponseDto sol1 = UserSubmissionResponseDto.builder()
                .userId(userId)
                .challengeId("d43a1a4d-ee8f-432d-8f9c-68eda2547dae")
                .languageId("409c9fe8-74de-4db3-81a1-a55280cf92ef")
                .solutionText("This is the submitted solution")
                .status("SUBMITTED")
                .build();

        UserSubmissionResponseDto sol2 = UserSubmissionResponseDto.builder()
                .userId(userId)
                .challengeId("b5c06903-f27b-4057-8220-ad9d957cdce4")
                .languageId("09fabe32-7362-4bfb-ac05-b7bf854c6e0f")
                .solutionText("This is the submitted solution")
                .status("SUBMITTED")
                .build();

        when(userSubmissionService.getAllSubmissionsByUser(userId))
                .thenReturn(Flux.just(sol1, sol2));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/solutions", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserSubmissionResponseDto.class)
                .hasSize(2)
                .value(list -> {
                    Assertions.assertEquals(sol1.getUserId(), list.get(0).getUserId());
                    Assertions.assertEquals(sol2.getUserId(), list.get(1).getUserId());
                });

        verify(userSubmissionService, times(1)).getAllSubmissionsByUser(userId);
    }

    @Test
    void getAllSolutions_returns404IfNotFound() {
        String userId = UUID.randomUUID().toString();

        when(userSubmissionService.getAllSubmissionsByUser(userId))
                .thenReturn(Flux.error(new NotFoundException("Solutions not found")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/solutions", userId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("Solutions not found");

        verify(userSubmissionService, times(1)).getAllSubmissionsByUser(userId);
    }

    @Test
    void getAllSolutions_returns400IfInvalidUUID() {
        String badUserId = "not-a-uuid";

        when(userSubmissionService.getAllSubmissionsByUser(badUserId))
                .thenReturn(Flux.error(new BadUUIDException("Bad UUID")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/solutions", badUserId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("The provided IDs are not valid.");

        verify(userSubmissionService, times(1)).getAllSubmissionsByUser(badUserId);
    }

    @Test
    void getAllSolutions_returns500IfUnexpectedError() {
        String userId = UUID.randomUUID().toString();

        when(userSubmissionService.getAllSubmissionsByUser(userId))
                .thenReturn(Flux.error(new RuntimeException("Boom")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/solutions", userId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Unexpected error happened.");

        verify(userSubmissionService, times(1)).getAllSubmissionsByUser(userId);
    }
}


