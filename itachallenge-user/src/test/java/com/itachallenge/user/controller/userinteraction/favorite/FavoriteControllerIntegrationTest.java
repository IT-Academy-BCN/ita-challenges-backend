package com.itachallenge.user.controller.userinteraction.favorite;


import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.repository.UserRepository;
import com.itachallenge.user.service.ExternalGithubService;
import com.itachallenge.userinteraction.repository.favorite.FavoriteRepository;

import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static reactor.core.publisher.Mono.when;

@SpringBootTest(  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Testcontainers
class FavoriteControllerIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0.9")
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl("itachallenge_test"));
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private UserRepository userRepository;

    @MockBean
    private ExternalGithubService externalGithubService;

    @BeforeEach
    void setUp(){
        favoriteRepository.deleteAll().block();
        userRepository.deleteAll().block();
        when(externalGithubService.userExists(anyString()))
                .thenReturn(Mono.just(true));
    }

    @Test
    void getUserFavorites_WithExistingFavorites_ReturnsSetOfChallengeIds(){
        String userId = createUser("user");

        String challengeId1 = UUID.randomUUID().toString();
        String challengeId2 = UUID.randomUUID().toString();
        String challengeId3 = UUID.randomUUID().toString();

        addFavorite(userId, challengeId1);
        addFavorite(userId, challengeId2);
        addFavorite(userId, challengeId3);

        webTestClient.get()
                .uri("/itachallenge/api/v1/userinteraction/favorites/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<Set<String>>(){})
                .value(
                        favorites -> {
                            assertThat(favorites).hasSize(3);
                            assertThat(favorites).containsExactlyInAnyOrder(challengeId1, challengeId2, challengeId3);
                        }
                );
    }

    @Test
    void getUserFavorites_WithNoFavorites_ReturnsEmptySet(){
        String userId = createUser("user");

        webTestClient.get()
                .uri("/itachallenge/api/v1/userinteraction/favorites/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<Set<String>>(){})
                .value(
                        favorites -> assertThat(favorites).isEmpty()
                );
    }

    @Test
    void getUserFavorites_WithMultipleFavoritesFromDifferentUsers_ReturnsOnlyUserFavorites(){
        String user1 = createUser("user1");
        String user2 = createUser("user2");

        String challengeId1 = UUID.randomUUID().toString();
        String challengeId2 = UUID.randomUUID().toString();
        String challengeId3 = UUID.randomUUID().toString();

        addFavorite(user1, challengeId1);
        addFavorite(user1, challengeId2);
        addFavorite(user2, challengeId3);

        webTestClient.get()
                .uri("/itachallenge/api/v1/userinteraction/favorites/{userId}", user1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<Set<String>>() {})
                .value(favorites -> {
                            assertThat(favorites).hasSize(2);
                            assertThat(favorites).containsExactlyInAnyOrder(challengeId1, challengeId2);
                            assertThat(favorites).doesNotContain(challengeId3);
                        }
                );
    }

    @Test
    void getUserFavorites_WithInvalidUUID_Returns400(){
        webTestClient.get()
                .uri("/itachallenge/api/v1/userinteraction/favorites/{userId}", 321)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                //TODO UN-COMMENT WHEN NEW ERROR-CORE GLOBAL HANDLER IS USED + QUITTING 3 LAST LINES
//              .expectBody()
//                .jsonPath("$.status").isEqualTo(400)
//                .jsonPath("$.error").value(error -> assertThat(error.toString())
//                        .contains("BadUUIDException"))
//                .jsonPath("$.message").value(message -> assertThat(message.toString())
//                        .contains("The provided IDs are not valid"))
//                .jsonPath("$.path").value(path -> assertThat(path.toString())
//                        .contains("/users/" + invalidUserId + "/favorites"));
                .expectBody(String.class)
                .value( body ->
                        assertThat(body).contains("The provided IDs are not valid"));
    }

    @Test
    void getUserFavorites_WhenUserDoesntExist_Returns404(){
        String nonExistentUserId = UUID.randomUUID().toString();

        webTestClient.get()
                .uri("/itachallenge/api/v1/userinteraction/favorites/{userId}", nonExistentUserId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                //TODO UN-COMMENT WHEN NEW ERROR-CORE GLOBAL HANDLER IS USED + QUITTING 3 LAST LINES
//              .expectBody()
//                .jsonPath("$.status").isEqualTo(404)
//                .jsonPath("$.error").value(error -> assertThat(error.toString())
//                        .contains("NotFoundException")) // Nom de l'exception
//                .jsonPath("$.message").value(message -> assertThat(message.toString())
//                        .contains("not found")) // Message d'erreur
//                .jsonPath("$.path").value(path -> assertThat(path.toString())
//                        .contains("/users/" + invalidUserId + "/favorites"));
                .expectBody(String.class)
                .value( body ->
                        assertThat(body).contains("not found"));
    }



    private String createUser(String user){
        AdminCreateUserRequestDto userRequestDto = new AdminCreateUserRequestDto();
        userRequestDto.setUsername(user);

        AdminCreateUserResponseDto userResponseDto =
                webTestClient.post()
                        .uri("/itachallenge/api/v1/admin/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userRequestDto)
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(AdminCreateUserResponseDto.class)
                        .returnResult().getResponseBody();

        assertThat(userResponseDto).isNotNull();
        assertThat(userResponseDto.getUserId()).isNotNull();
        return userResponseDto.getUserId();
    }

    private void addFavorite(String userId, String challengeId){
        webTestClient.post()
                .uri("/itachallenge/api/v1/userinteraction/favorites/users/{userId}/favorites/{challengeId}", userId, challengeId)
                .exchange()
                .expectStatus().isCreated();
    }
}
