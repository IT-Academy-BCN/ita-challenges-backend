package com.itachallenge.user.controller.userinteraction.favorite;

import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.repository.UserRepository;
import com.itachallenge.userinteraction.repository.favorite.FavoriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

    @BeforeEach
    void setUp() {
        favoriteRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }

    @Test
    void getUserFavorites_WithExistingFavorites_ReturnsSetOfChallengeIds() {
        String userId = createUser("user");
        String challengeId1 = UUID.randomUUID().toString();
        String challengeId2 = UUID.randomUUID().toString();

        addFavorite(userId, challengeId1);
        addFavorite(userId, challengeId2);

        webTestClient.get()
                .uri("/itachallenge/api/v1/users/{userId}/favorites", userId) // Nueva ruta
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<Set<String>>() {})
                .value(favorites -> {
                    assertThat(favorites).hasSize(2);
                    assertThat(favorites).containsExactlyInAnyOrder(challengeId1, challengeId2);
                });
    }

    @Test
    void getUserFavorites_WithInvalidUUID_Returns400() {
        webTestClient.get()
                .uri("/itachallenge/api/v1/users/{userId}/favorites", "invalid-uuid") // Nueva ruta
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("The provided IDs are not valid"));
    }

    @Test
    void deleteFavorite_WithExistingFavorite_ReturnsOk() {
        String userId = createUser("user");
        String challengeId = UUID.randomUUID().toString();
        addFavorite(userId, challengeId);

        webTestClient.delete()
                .uri("/itachallenge/api/v1/users/{userId}/favorites/{challengeId}", userId, challengeId) // Nueva ruta
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(true);
    }

    private String createUser(String user) {
        AdminCreateUserRequestDto userRequestDto = new AdminCreateUserRequestDto();
        userRequestDto.setUsername(user);

        AdminCreateUserResponseDto userResponseDto = webTestClient.post()
                .uri("/itachallenge/api/v1/admin/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRequestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AdminCreateUserResponseDto.class)
                .returnResult().getResponseBody();

        assertThat(userResponseDto).isNotNull();
        return userResponseDto.getUserId();
    }

    private void addFavorite(String userId, String challengeId) {
        webTestClient.post()
                .uri("/itachallenge/api/v1/users/{userId}/favorites/{challengeId}", userId, challengeId)
                .exchange()
                .expectStatus().isCreated();
    }
}