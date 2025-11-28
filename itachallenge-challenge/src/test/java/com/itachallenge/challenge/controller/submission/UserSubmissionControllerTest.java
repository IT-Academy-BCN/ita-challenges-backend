package com.itachallenge.challenge.controller.submission;

import com.itachallenge.challenge.exception.GlobalExceptionHandler;
import com.itachallenge.challenge.exception.NotFoundException;
import com.itachallenge.submission.service.IUserSubmissionService;
import com.itachallenge.challenge.dto.submission.UserSubmissionResponseDto;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.never;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserSubmissionControllerTest {

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
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    void getAllSubmissions_returnsSubmissions() {
        String userId = UUID.randomUUID().toString();

        UserSubmissionResponseDto sol1 = UserSubmissionResponseDto.builder()
                .userId(userId)
                .challengeId("d43a1a4d-ee8f-432d-8f9c-68eda2547dae")
                .languageId("409c9fe8-74de-4db3-81a1-a55280cf92ef")
                .submissionText("This is the submitted solution")
                .action("GIVE_UP")
                .build();

        UserSubmissionResponseDto sol2 = UserSubmissionResponseDto.builder()
                .userId(userId)
                .challengeId("b5c06903-f27b-4057-8220-ad9d957cdce4")
                .languageId("09fabe32-7362-4bfb-ac05-b7bf854c6e0f")
                .submissionText("This is the submitted solution")
                .action("GIVE_UP")
                .build();

        when(userSubmissionService.getAllSubmissionsByUser(userId))
                .thenReturn(Flux.just(sol1, sol2));

        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/challenges/{userId}/submissions", userId)
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
    void getAllSubmissions_returns200WithEmptyArrayWhenNoSubmissions() {
        String validUserId = "123e4567-e89b-12d3-a456-426614174000";

        when(userSubmissionService.getAllSubmissionsByUser(validUserId))
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/challenges/{userId}/submissions", validUserId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").isEmpty();

        verify(userSubmissionService).getAllSubmissionsByUser(validUserId);
    }

    @Test
    void getAllSubmissions_returns400IfInvalidUUID() {
        String badUserId = "not-a-uuid";

        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/challenges/{userId}/submissions", badUserId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid UUID format");


        verify(userSubmissionService, never()).getAllSubmissionsByUser(badUserId);
    }

    @Test
    void getAllSubmissions_returns500IfUnexpectedError() {
        String userId = UUID.randomUUID().toString();

        when(userSubmissionService.getAllSubmissionsByUser(userId))
                .thenReturn(Flux.error(new RuntimeException("Boom")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/challenge/challenges/{userId}/submissions", userId)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .json("");

        verify(userSubmissionService, times(1)).getAllSubmissionsByUser(userId);
    }
}


