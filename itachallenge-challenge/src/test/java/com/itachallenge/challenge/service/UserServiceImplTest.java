package com.itachallenge.challenge.service;

import com.itachallenge.challenge.exception.BadRequestException;
import com.itachallenge.challenge.exception.InternalServerErrorException;
import com.itachallenge.challenge.exception.UserNotFoundException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceImplTest {

    private MockWebServer mockWebServer;
    private UserServiceImpl userService;

    private static final String FAVORITES_URL = "/itachallenge/api/v1/user/users/%s/favorites/%s";
    private static final String BOOKMARKS_URL = "/itachallenge/api/v1/user/users/%s/bookmarks/%s";
    public static final String X_FAVORITE_MESSAGE = "X-Favorite-Message";
    public static final String X_BOOKMARK_MESSAGE = "X-Bookmark-Message";

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        userService = new UserServiceImpl(
                WebClient.builder(),
                mockWebServer.url("").toString()
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.close();
    }

    @Test
    void addChallengeToFavorites_AddedToUser_ReturnsTrue() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";

        mockWebServer.enqueue(new MockResponse()
                .setBody("true")
                .setResponseCode(201)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.addChallengeToFavorites(userId, challengeId);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(FAVORITES_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
    }

    @Test
    void addChallengeToBookmarks_AddedToUser_ReturnsTrue() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";

        mockWebServer.enqueue(new MockResponse()
                .setBody("true")
                .setResponseCode(201)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.addChallengeToBookmarks(userId, challengeId);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(BOOKMARKS_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
        assertEquals("POST", request.getMethod());
    }

    @Test
    void addChallengeToFavorites_NotAddedToUser_ReturnsFalse() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.addChallengeToFavorites(userId, challengeId);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(FAVORITES_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
    }

    @Test
    void addChallengeToBookmarks_NotAddedToUser_ReturnsFalse() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.addChallengeToBookmarks(userId, challengeId);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(BOOKMARKS_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
        assertEquals("POST", request.getMethod());
    }

    @Test
    void addChallengeToFavorites_BadRequest_ReturnsError() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";
        String someErrorMessage = "Some error message";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(400)
                .addHeader(X_FAVORITE_MESSAGE, someErrorMessage)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.addChallengeToFavorites(userId, challengeId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadRequestException.class, throwable);
                    assertTrue(throwable.getMessage().contains(someErrorMessage));
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(FAVORITES_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
    }

    @Test
    void addChallengeToBookmarks_BadRequest_ReturnsError() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";
        String someErrorMessage = "Some error message";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(400)
                .addHeader(X_BOOKMARK_MESSAGE, someErrorMessage)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.addChallengeToBookmarks(userId, challengeId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadRequestException.class, throwable);
                    assertTrue(throwable.getMessage().contains(someErrorMessage));
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(BOOKMARKS_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
        assertEquals("POST", request.getMethod());
    }

    @Test
    void addChallengeToFavorites_UserNotFound_ReturnsError() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.addChallengeToFavorites(userId, challengeId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(UserNotFoundException.class, throwable);
                    assertTrue(throwable.getMessage().contains("User not found"));
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(FAVORITES_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
    }

    @Test
    void addChallengeToBookmarks_UserNotFound_ReturnsError() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.addChallengeToBookmarks(userId, challengeId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(UserNotFoundException.class, throwable);
                    assertTrue(throwable.getMessage().contains("User not found"));
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(BOOKMARKS_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
        assertEquals("POST", request.getMethod());
    }

    @Test
    void addChallengeToFavorites_500_ReturnsError() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";
        String someErrorMessage = "Some error message";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(500)
                .addHeader(X_FAVORITE_MESSAGE, someErrorMessage)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.addChallengeToFavorites(userId, challengeId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(InternalServerErrorException.class, throwable);
                    assertTrue(throwable.getMessage().contains(throwable.getMessage()));
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(FAVORITES_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
    }

    @Test
    void addChallengeToBookmarks_500_ReturnsError() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";
        String someErrorMessage = "Some error message";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(500)
                .addHeader(X_BOOKMARK_MESSAGE, someErrorMessage)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.addChallengeToBookmarks(userId, challengeId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(InternalServerErrorException.class, throwable);
                    assertTrue(throwable.getMessage().contains(someErrorMessage));
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(BOOKMARKS_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
        assertEquals("POST", request.getMethod());
    }

    @Test
    void removeChallengeFromFavorites_DeletedFromUser_ReturnsTrue() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";

        mockWebServer.enqueue(new MockResponse()
                .setBody("true")
                .setResponseCode(201)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.removeChallengeFromFavorites(userId, challengeId);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(FAVORITES_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
        assertEquals("DELETE", request.getMethod());
    }

    @Test
    void removeChallengeFromFavorites_NotDeletedFromUser_ReturnsFalse() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.removeChallengeFromFavorites(userId, challengeId);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(FAVORITES_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
        assertEquals("DELETE", request.getMethod());
    }

    @Test
    void removeChallengeFromFavorites_BadRequest_ReturnsError() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";
        String someErrorMessage = "Some error message";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(400)
                .addHeader(X_FAVORITE_MESSAGE, someErrorMessage)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.removeChallengeFromFavorites(userId, challengeId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadRequestException.class, throwable);
                    assertTrue(throwable.getMessage().contains(someErrorMessage));
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(FAVORITES_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
        assertEquals("DELETE", request.getMethod());
    }

    @Test
    void removeChallengeFromFavorites_UserNotFound_ReturnsError() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.removeChallengeFromFavorites(userId, challengeId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(UserNotFoundException.class, throwable);
                    assertTrue(throwable.getMessage().contains("User not found"));
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(FAVORITES_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
        assertEquals("DELETE", request.getMethod());
    }

    @Test
    void removeChallengeFromFavorites_500_ReturnsError() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";
        String someErrorMessage = "Some error message";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(500)
                .addHeader(X_FAVORITE_MESSAGE, someErrorMessage)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.removeChallengeFromFavorites(userId, challengeId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(InternalServerErrorException.class, throwable);
                    assertTrue(throwable.getMessage().contains(throwable.getMessage()));
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(FAVORITES_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
        assertEquals("DELETE", request.getMethod());
    }

    @Test
    void removeChallengeFromBookmarks_DeletedFromUser_ReturnsTrue() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";

        mockWebServer.enqueue(new MockResponse()
                .setBody("true")
                .setResponseCode(201)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.removeChallengeFromBookmarks(userId, challengeId);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(BOOKMARKS_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
        assertEquals("DELETE", request.getMethod());
    }

    @Test
    void removeChallengeFromBookmarks_NotDeletedFromUser_ReturnsFalse() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.removeChallengeFromBookmarks(userId, challengeId);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(BOOKMARKS_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
        assertEquals("DELETE", request.getMethod());
    }

    @Test
    void removeChallengeFromBookmarks_BadRequest_ReturnsError() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";
        String someErrorMessage = "Some error message";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(400)
                .addHeader(X_BOOKMARK_MESSAGE, someErrorMessage)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.removeChallengeFromBookmarks(userId, challengeId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadRequestException.class, throwable);
                    assertTrue(throwable.getMessage().contains(someErrorMessage));
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(BOOKMARKS_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
        assertEquals("DELETE", request.getMethod());
    }

    @Test
    void removeChallengeFromBookmarks_UserNotFound_ReturnsError() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.removeChallengeFromBookmarks(userId, challengeId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(UserNotFoundException.class, throwable);
                    assertTrue(throwable.getMessage().contains("User not found"));
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(BOOKMARKS_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
        assertEquals("DELETE", request.getMethod());
    }

    @Test
    void removeChallengeFromBookmarks_500_ReturnsError() throws InterruptedException {
        String userId = "someId";
        String challengeId = "anotherId";
        String someErrorMessage = "Some error message";

        mockWebServer.enqueue(new MockResponse()
                .setBody("false")
                .setResponseCode(500)
                .addHeader(X_BOOKMARK_MESSAGE, someErrorMessage)
                .addHeader("Content-Type", "application/json"));

        Mono<Boolean> result = userService.removeChallengeFromBookmarks(userId, challengeId);

        StepVerifier.create(result)
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(InternalServerErrorException.class, throwable);
                    assertTrue(throwable.getMessage().contains(throwable.getMessage()));
                })
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request.getRequestUrl());
        assertEquals(
                String.format(BOOKMARKS_URL, userId, challengeId),
                request.getRequestUrl().encodedPath());
        assertEquals("DELETE", request.getMethod());
    }

}
