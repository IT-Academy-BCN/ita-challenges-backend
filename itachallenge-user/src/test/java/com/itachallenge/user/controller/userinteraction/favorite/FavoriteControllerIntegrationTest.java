package com.itachallenge.user.controller.userinteraction.favorite;


import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.repository.UserRepository;
import com.itachallenge.userinteraction.document.favorite.FavoriteDocument;
import com.itachallenge.userinteraction.repository.favorite.FavoriteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.profiles.active=test")
@AutoConfigureWebTestClient
class FavoriteControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        favoriteRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }

    @AfterEach
    void tearDown(){
        favoriteRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }

    @Test
    void getUserFavorites_WithExistingFavorites_ReturnsSetOfChallengeIds(){
        UUID userId = UUID.randomUUID();

        UserDocument user = UserDocument.builder()
                .uuid(userId)
                .username("test_user")
                .role(Role.USER)
                .points(0)
                .build();

        userRepository.save(user).block();

        UUID challengeId1 = UUID.randomUUID();
        UUID challengeId2 = UUID.randomUUID();
        UUID challengeId3 = UUID.randomUUID();

        FavoriteDocument favorite1 = FavoriteDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId1)
                .createdAt(LocalDateTime.now())
                .build();

        FavoriteDocument favorite2 = FavoriteDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId2)
                .createdAt(LocalDateTime.now())
                .build();

        FavoriteDocument favorite3 = FavoriteDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId3)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.saveAll(List.of(favorite1, favorite2, favorite3)).blockLast();

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<Set<String>>() {})
                .value(favorites ->{
                    assertThat(favorites).hasSize(3);
                    assertThat(favorites).containsExactlyInAnyOrder(challengeId1.toString(), challengeId2.toString(), challengeId3.toString());
                });
    }

    @Test
    void getUserFavorites_WithNoFavorites_ReturnsEmptySet(){
        UUID userId = UUID.randomUUID();

        UserDocument user = UserDocument.builder()
                .uuid(userId)
                .username("test_user")
                .role(Role.USER)
                .points(0)
                .build();

        userRepository.save(user).block();

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<Set<String>>() {})
                .value( favorites ->
                        assertThat(favorites).isEmpty());
    }

    @Test
    void getUserFavorites_WithMultipleFavoritesFromDifferentUsers_ReturnsOnlyUserFavorites(){
        UserDocument user1 = UserDocument.builder()
                .uuid(UUID.randomUUID())
                .username("name 1")
                .role(Role.USER)
                .build();

        UserDocument user2 = UserDocument.builder()
                .uuid(UUID.randomUUID())
                .username("name 2")
                .role(Role.USER)
                .build();

        userRepository.saveAll(List.of(user1, user2)).blockLast();

        UUID challengeId1 = UUID.randomUUID();
        UUID challengeId2 = UUID.randomUUID();
        UUID challengeId3 = UUID.randomUUID();

        FavoriteDocument favorite1 = FavoriteDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(user1.getUuid())
                .challengeId(challengeId1)
                .createdAt(LocalDateTime.now())
                .build();

        FavoriteDocument favorite2 = FavoriteDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(user1.getUuid())
                .challengeId(challengeId2)
                .createdAt(LocalDateTime.now())
                .build();

        FavoriteDocument favorite3 = FavoriteDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(user2.getUuid())
                .challengeId(challengeId3)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.saveAll(List.of(favorite1, favorite2, favorite3)).blockLast();

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", user1.getUuid())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<Set<String>>() {})
                .value(favorites ->{
                    assertThat(favorites).hasSize(2);
                    assertThat(favorites).containsExactlyInAnyOrder(challengeId1.toString(), challengeId2.toString());
                    assertThat(favorites).doesNotContain(challengeId3.toString());
                });

    }

    @Test
    void getUserFavorites_WithInvalidUUID_Returns400() {
        String invalidUserId = "invalid-uuid-format";

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", invalidUserId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
//                .expectBody()   //// TO UN-COMMENT WHEN NEW ERROR-CORE GLOBAL HANDLER IS USED + QUItTING 3 LAST LINES
//                .jsonPath("$.status").isEqualTo(400)
//                .jsonPath("$.error").value(error -> assertThat(error.toString())
//                        .contains("BadUUIDException"))
//                .jsonPath("$.message").value(message -> assertThat(message.toString())
//                        .contains("The provided IDs are not valid"))
//                .jsonPath("$.path").value(path -> assertThat(path.toString())
//                        .contains("/users/" + invalidUserId + "/favorites"));
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body).contains("The provided IDs are not valid");
                });
    }

    @Test
    void getUserFavorites_WithMalformedUUID_Returns400() {
        String invalidUserId = "++++++-123e4567";

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", invalidUserId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
//                .expectBody() //// TO UN-COMMENT WHEN NEW ERROR-CORE GLOBAL HANDLER IS USED + QUITTING 3 LAST LINES
//                .jsonPath("$.status").isEqualTo(400)
//                .jsonPath("$.error").value(error -> assertThat(error.toString())
//                        .contains("BadUUIDException"))
//                .jsonPath("$.message").value(message -> assertThat(message.toString())
//                        .contains("The provided IDs are not valid"))
//                .jsonPath("$.path").value(path -> assertThat(path.toString())
//                        .contains("/users/" + invalidUserId + "/favorites"));
                .expectBody(String.class)
                .value(body ->
                    assertThat(body).contains("The provided IDs are not valid"));
    }

    @Test
    void getUserFavorites_WhenUserDoesntExists_Returns404() {
        String invalidUserId = "123e4567-e89b-12d3-a456-42661417400";

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", invalidUserId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
//                .expectBody()   //// TO UN-COMMENT WHEN NEW ERROR-CORE GLOBAL HANDLER IS USED + QUITTING 3 LAST LINES
//                .jsonPath("$.status").isEqualTo(404)
//                .jsonPath("$.error").value(error -> assertThat(error.toString())
//                        .contains("NotFoundException")) // Nom de l'exception
//                .jsonPath("$.message").value(message -> assertThat(message.toString())
//                        .contains("not found")) // Message d'erreur
//                .jsonPath("$.path").value(path -> assertThat(path.toString())
//                        .contains("/users/" + invalidUserId + "/favorites"));
                .expectBody(String.class)
                .value(body ->
                        assertThat(body).contains("not found"));
    }

}
