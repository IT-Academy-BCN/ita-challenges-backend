package com.itachallenge.user.controller;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.dto.UserSolutionResponseDto;
import com.itachallenge.user.service.IUserSolutionService;
import com.itachallenge.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = UserController.class)
@ContextConfiguration(classes = { UserController.class })
class UserControllerHappyPathTest {

    @MockBean
    private UserService userService;
    @MockBean
    private IUserSolutionService userSolutionService;
    @InjectMocks private UserController userController;

    @Autowired
    private WebTestClient webTestClient;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void testEndpoint_ShouldReturnHelloMessage() {
        webTestClient.get()
                .uri("/itachallenge/api/v1/user/test")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello from ITA Challenge UserController!!!");
    }

    @Test
    void getUser_WhenUserExists_Returns200() {
        String githubUsername = "existingUser";
        UserDocument expectedUser = new UserDocument(UUID.randomUUID(), githubUsername, Role.ADMIN, null, null, 0);
        when(userService.getUser(githubUsername)).thenReturn(Mono.just(expectedUser));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/" + githubUsername)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDocument.class).isEqualTo(expectedUser);

        verify(userService).getUser(githubUsername);
    }

    @Test
    void addToFavorites_WhenAdded_Returns201() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToFavorites(userId, challengeId)).thenReturn(Mono.just(true));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Boolean.class).isEqualTo(true);
    }

    @Test
    void addToBookmarks_WhenAdded_Returns201() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToBookmarks(userId, challengeId)).thenReturn(Mono.just(true));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Boolean.class).isEqualTo(true);
    }

    @Test
    void addToFavorites_WhenAlreadyInFavorites_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToFavorites(userId, challengeId)).thenReturn(Mono.just(false));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);
    }

    @Test
    void addToBookmarks_WhenAlreadyInBookmarks_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToBookmarks(userId, challengeId)).thenReturn(Mono.just(false));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);
    }

    @Test
    void deleteFromFavorites_WhenDeleted_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromFavorites(userId, challengeId)).thenReturn(Mono.just(true));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(true);
    }

    @Test
    void deleteFromBookmarks_WhenDeleted_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromBookmarks(userId, challengeId)).thenReturn(Mono.just(true));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(true);
    }

    @Test
    void deleteFromFavorites_WhenNotInFavorites_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromFavorites(userId, challengeId)).thenReturn(Mono.just(false));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);
    }

    @Test
    void deleteFromBookmarks_WhenNotInBookmarks_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromBookmarks(userId, challengeId)).thenReturn(Mono.just(false));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);
    }

    @Test
    void getUserFavorites_returnsFavorites() {
        UUID userId = UUID.randomUUID();
        Set<UUID> expected = Set.of(UUID.randomUUID(), UUID.randomUUID());
        when(userService.getUserFavorites(userId.toString())).thenReturn(Mono.just(expected));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UUID.class)
                .hasSize(expected.size())
                .contains(expected.toArray(new UUID[0]));
    }

    @Test
    void getUserBookmarks_returnsBookmarks() {
        UUID userId = UUID.randomUUID();
        Set<UUID> expected = Set.of(UUID.randomUUID(), UUID.randomUUID());
        when(userService.getUserBookmarks(userId.toString())).thenReturn(Mono.just(expected));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/bookmarks", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UUID.class)
                .hasSize(expected.size())
                .contains(expected.toArray(new UUID[0]));
    }

    @Test
    void getAllSolutions_returnsSolutions() {
        String userId = UUID.randomUUID().toString();
        UserSolutionResponseDto s1 = UserSolutionResponseDto.builder()
                .userId(userId).challengeId("c1").languageId("l1").solutionText("t1").build();
        UserSolutionResponseDto s2 = UserSolutionResponseDto.builder()
                .userId(userId).challengeId("c2").languageId("l2").solutionText("t2").build();
        when(userSolutionService.getAllSolutionsByUser(userId)).thenReturn(Flux.just(s1, s2));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/solutions", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserSolutionResponseDto.class).hasSize(2);
    }
}
