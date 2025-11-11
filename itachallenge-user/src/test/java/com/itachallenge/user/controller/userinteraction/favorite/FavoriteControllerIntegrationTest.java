package com.itachallenge.user.controller.userinteraction.favorite;


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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FavoriteControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @BeforeEach
    void setUp(){
        favoriteRepository.deleteAll().block();
    }

    @AfterEach
    void tearDown(){
        favoriteRepository.deleteAll().block();
    }

    @Test
    void getUserFavorites_WithExistingFavorites_ReturnsSetOfChallengeIds(){
        UUID userId = UUID.randomUUID();

        UUID challengeId1 = UUID.randomUUID();
        UUID challengeId2 = UUID.randomUUID();
        UUID challengeId3 = UUID.randomUUID();

        FavoriteDocument favorite1 = FavoriteDocument.builder()
                .userId(userId)
                .challengeId(challengeId1)
                .createdAt(LocalDateTime.now())
                .build();

        FavoriteDocument favorite2 = FavoriteDocument.builder()
                .userId(userId)
                .challengeId(challengeId2)
                .createdAt(LocalDateTime.now())
                .build();

        FavoriteDocument favorite3 = FavoriteDocument.builder()
                .userId(userId)
                .challengeId(challengeId3)
                .createdAt(LocalDateTime.now())
                .build();

        favoriteRepository.saveAll(List.of(favorite1, favorite2, favorite3)).blockLast();

        webTestClient.get()
                .uri("/users/{userId}/favorites", userId)
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

        webTestClient.get()
                .uri("/users/{userId}/favorites", userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<Set<String>>() {})
                .value( favorites ->
                        assertThat(favorites).isEmpty());

    }

}
