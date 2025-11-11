package com.itachallenge.user.controller.userinteraction.favorite;


import com.itachallenge.userinteraction.repository.favorite.FavoriteRepository;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FavoriteControllerIntegrationTest {

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


    }

}
