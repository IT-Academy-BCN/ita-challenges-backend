package com.itachallenge.user.controller.userinteraction.bookmark;

import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.repository.UserRepository;
import com.itachallenge.userinteraction.repository.bookmark.BookmarkRepository;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Testcontainers
class BookmarkControllerIntegrationTest {

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
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        bookmarkRepository.deleteAll().block();
        userRepository.deleteAll().block();
    }


    @Test
    void getUserBookmarks_WithExistingBookmarks_ReturnsSetOfChallengeIds(){
        String userId = createUser("user");

        String challengeId1 = UUID.randomUUID().toString();
        String challengeId2 = UUID.randomUUID().toString();
        String challengeId3 = UUID.randomUUID().toString();

        addBookmark(userId, challengeId1); // Usar el método addBookmark
        addBookmark(userId, challengeId2);
        addBookmark(userId, challengeId3);

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/bookmarks", userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<Set<String>>(){})
                .value(
                        bookmarks -> {
                            assertThat(bookmarks).hasSize(3);
                            assertThat(bookmarks).containsExactlyInAnyOrder(challengeId1, challengeId2, challengeId3);
                        }
                );
    }

    @Test
    void getUserBookmarks_WithNoBookmarks_ReturnsEmptySet(){
        String userId = createUser("user");

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/bookmarks", userId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<Set<String>>(){})
                .value(
                        bookmarks -> assertThat(bookmarks).isEmpty()
                );
    }

    @Test
    void getUserBookmarks_WithMultipleBookmarksFromDifferentUsers_ReturnsOnlyUserBookmarks(){
        String user1 = createUser("user1");
        String user2 = createUser("user2");

        String challengeId1 = UUID.randomUUID().toString();
        String challengeId2 = UUID.randomUUID().toString();
        String challengeId3 = UUID.randomUUID().toString();

        addBookmark(user1, challengeId1);
        addBookmark(user1, challengeId2);
        addBookmark(user2, challengeId3);

        webTestClient.get()
                // Cambiar el endpoint a /bookmarks
                .uri("/itachallenge/api/v1/user/users/{userId}/bookmarks", user1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<Set<String>>() {})
                .value(bookmarks -> {
                            assertThat(bookmarks).hasSize(2);
                            assertThat(bookmarks).containsExactlyInAnyOrder(challengeId1, challengeId2);
                            assertThat(bookmarks).doesNotContain(challengeId3);
                        }
                );
    }

    @Test
    void getUserBookmarks_WithInvalidUUID_Returns400(){
        webTestClient.get()
                // Cambiar el endpoint a /bookmarks
                .uri("/itachallenge/api/v1/user/users/{userId}/bookmarks", 321)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .value( body ->
                        assertThat(body).contains("The provided IDs are not valid"));
    }

    @Test
    void getUserBookmarks_WhenUserDoesntExist_Returns404(){ // Adaptar nombre
        String nonExistentUserId = UUID.randomUUID().toString();

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/bookmarks", nonExistentUserId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
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

    private void addBookmark(String userId, String challengeId){
        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/{userId}/bookmarks/{challengeId}", userId, challengeId)
                .exchange()
                .expectStatus().isCreated();
    }
}
