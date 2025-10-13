package com.itachallenge.user.controller;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.dto.UserSolutionResponseDto;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.exception.UserGlobalExceptionHandler;
import com.itachallenge.user.service.IUserSolutionService;
import com.itachallenge.user.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private IUserSolutionService userSolutionService;

    @InjectMocks
    private UserController userController;

    private WebTestClient webTestClient;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(userController)
                .controllerAdvice(new UserGlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
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
        UserDocument expectedUser = new UserDocument(UUID.randomUUID(), githubUsername, Role.ADMIN, null, null);
        when(userService.getUser(githubUsername)).thenReturn(Mono.just(expectedUser));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/" + githubUsername)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Validation-Status")
                .expectHeader().valueEquals("X-Validation-Status", "Success")
                .expectHeader().valueEquals("X-Github-Username", githubUsername)
                .expectBody(UserDocument.class).isEqualTo(expectedUser);

        verify(userService, times(1)).getUser(githubUsername);
    }

    @Test
    void getUser_WhenUserNotExists_Returns404() {
        String githubUsername = "nonExistentUser";
        when(userService.getUser(githubUsername)).thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/" + githubUsername)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).isEqualTo("User not found");

        verify(userService, times(1)).getUser(githubUsername);
    }

    @Test
    void getUser_WhenServiceReturnsError_Returns500() {
        String githubUsername = "username";
        when(userService.getUser(any(String.class))).thenReturn(Mono.error( new RuntimeException()));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/" + githubUsername)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(String.class).isEqualTo("Unexpected error happened.");

        verify(userService, times(1)).getUser(githubUsername);
    }

    @Test
    void addToFavorites_WhenAdded_Returns201() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToFavorites(userId, challengeId))
                .thenReturn(Mono.just(true));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectBody(Boolean.class).isEqualTo(true);

        verify(userService, times(1)).addChallengeToFavorites(userId, challengeId);
    }

    @Test
    void addToBookmarks_WhenAdded_Returns201() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToBookmarks(userId, challengeId))
                .thenReturn(Mono.just(true));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CREATED)
                .expectBody(Boolean.class).isEqualTo(true);

        verify(userService, times(1)).addChallengeToBookmarks(userId, challengeId);
    }

    @Test
    void addToFavorites_WhenAlreadyInFavorites_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToFavorites(userId, challengeId))
                .thenReturn(Mono.just(false));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).addChallengeToFavorites(userId, challengeId);
    }

    @Test
    void addToBookmarks_WhenAlreadyInBookmarks_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToBookmarks(userId, challengeId))
                .thenReturn(Mono.just(false));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).addChallengeToBookmarks(userId, challengeId);
    }

    @Test
    void addToFavorites_WhenUserNotExists_Returns404() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToFavorites(userId, challengeId))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).isEqualTo("User not found");

        verify(userService, times(1)).addChallengeToFavorites(userId, challengeId);
    }

    @Test
    void addToBookmarks_WhenUserNotExists_Returns404() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToBookmarks(userId, challengeId))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(String.class).isEqualTo("User not found");

        verify(userService, times(1)).addChallengeToBookmarks(userId, challengeId);
    }

    @Test
    void addToFavorites_WhenBadFormattedId_Returns400() {
        String userId = "invalidUuid";
        String challengeId = "invalidUUid";
        when(userService.addChallengeToFavorites(userId, challengeId))
                .thenReturn(Mono.error(new BadUUIDException("Error message")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("The provided IDs are not valid.");

        verify(userService, times(1)).addChallengeToFavorites(userId, challengeId);
    }

    @Test
    void addToBookmarks_WhenBadFormattedId_Returns400() {
        String userId = "invalidUuid";
        String challengeId = "invalidUUid";
        when(userService.addChallengeToBookmarks(userId, challengeId))
                .thenReturn(Mono.error(new BadUUIDException("Error message")));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(String.class).isEqualTo("The provided IDs are not valid.");

        verify(userService, times(1)).addChallengeToBookmarks(userId, challengeId);
    }

    @Test
    void addToFavorites_WhenUnexpectedError_Returns500() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToFavorites(userId, challengeId))
                .thenReturn(Mono.error(new Exception()));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(String.class).isEqualTo("Unexpected error happened.");

        verify(userService, times(1)).addChallengeToFavorites(userId, challengeId);
    }

    @Test
    void addToBookmarks_WhenUnexpectedError_Returns500() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.addChallengeToBookmarks(userId, challengeId))
                .thenReturn(Mono.error(new Exception()));

        webTestClient.post()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(String.class).isEqualTo("Unexpected error happened.");

        verify(userService, times(1)).addChallengeToBookmarks(userId, challengeId);
    }

    @Test
    void deleteFromFavorites_WhenDeleted_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromFavorites(userId, challengeId))
                .thenReturn(Mono.just(true));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(Boolean.class).isEqualTo(true);

        verify(userService, times(1)).deleteChallengeFromFavorites(userId, challengeId);
    }

    @Test
    void deleteFromBookmarks_WhenDeleted_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromBookmarks(userId, challengeId))
                .thenReturn(Mono.just(true));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.OK)
                .expectBody(Boolean.class).isEqualTo(true);

        verify(userService, times(1)).deleteChallengeFromBookmarks(userId, challengeId);
    }

    @Test
    void deleteFromFavorites_WhenNotInFavorites_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromFavorites(userId, challengeId))
                .thenReturn(Mono.just(false));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).deleteChallengeFromFavorites(userId, challengeId);
    }

    @Test
    void deleteFromBookmarks_WhenNotInBookmarks_Returns200() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromBookmarks(userId, challengeId))
                .thenReturn(Mono.just(false));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);

        verify(userService, times(1)).deleteChallengeFromBookmarks(userId, challengeId);
    }

    @Test
    void deleteFromFavorites_WhenUserNotExists_Returns404() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromFavorites(userId, challengeId))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(String.class).isEqualTo("User not found");

        verify(userService, times(1)).deleteChallengeFromFavorites(userId, challengeId);
    }

    @Test
    void deleteFromBookmarks_WhenUserNotExists_Returns404() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromBookmarks(userId, challengeId))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                .expectBody(String.class).isEqualTo("User not found");

        verify(userService, times(1)).deleteChallengeFromBookmarks(userId, challengeId);
    }

    @Test
    void deleteFromFavorites_WhenBadFormattedId_Returns404() {
        String userId = "invalidUuid";
        String challengeId = "invalidUUid";
        when(userService.deleteChallengeFromFavorites(userId, challengeId))
                .thenReturn(Mono.error(new BadUUIDException("Error message")));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(String.class).isEqualTo("The provided IDs are not valid.");

        verify(userService, times(1)).deleteChallengeFromFavorites(userId, challengeId);
    }

    @Test
    void deleteFromBookmarks_WhenBadFormattedId_Returns404() {
        String userId = "invalidUuid";
        String challengeId = "invalidUUid";
        when(userService.deleteChallengeFromBookmarks(userId, challengeId))
                .thenReturn(Mono.error(new BadUUIDException("Error message")));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(String.class).isEqualTo("The provided IDs are not valid.");

        verify(userService, times(1)).deleteChallengeFromBookmarks(userId, challengeId);
    }

    @Test
    void deleteFromFavorites_WhenUnexpectedError_Returns500() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromFavorites(userId, challengeId))
                .thenReturn(Mono.error(new Exception()));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/favorites/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(String.class).isEqualTo("Unexpected error happened.");

        verify(userService, times(1)).deleteChallengeFromFavorites(userId, challengeId);
    }

    @Test
    void deleteFromBookmarks_WhenUnexpectedError_Returns500() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        when(userService.deleteChallengeFromBookmarks(userId, challengeId))
                .thenReturn(Mono.error(new Exception()));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/user/users/" + userId + "/bookmarks/" + challengeId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(String.class).isEqualTo("Unexpected error happened.");

        verify(userService, times(1)).deleteChallengeFromBookmarks(userId, challengeId);
    }

    @Test
    @DisplayName("GET /users/{userId}/favorites returns favorite challenges")
    void getUserFavorites_returnsFavorites() {
        UUID userId = UUID.randomUUID();
        Set<UUID> expectedFavorites = Set.of(UUID.randomUUID(), UUID.randomUUID());

        when(userService.getUserFavorites(userId.toString())).thenReturn(Mono.just(expectedFavorites));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UUID.class)
                .hasSize(expectedFavorites.size())
                .contains(expectedFavorites.toArray(new UUID[0]));

        verify(userService, times(1)).getUserFavorites(userId.toString());
    }

    @Test
    @DisplayName("GET /users/{userId}/favorites returns 404 if user not found")
    void getUserFavorites_returns404IfUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userService.getUserFavorites(userId.toString()))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", userId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).isEqualTo("User not found");

        verify(userService, times(1)).getUserFavorites(userId.toString());
    }

    @Test
    @DisplayName("GET /users/{userId}/favorites returns 400 if UUID is invalid")
    void getUserFavorites_returns400IfInvalidUUID() {
        String invalidUserId = "invalid-uuid";

        when(userService.getUserFavorites(invalidUserId))
                .thenReturn(Mono.error(new BadUUIDException("The provided IDs are not valid.")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", invalidUserId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("The provided IDs are not valid.");

        verify(userService, times(1)).getUserFavorites(invalidUserId);
    }

    @Test
    @DisplayName("GET /users/{userId}/favorites returns 500 if there is an internal error")
    void getUserFavorites_returns500IfUnexpectedError() {
        UUID userId = UUID.randomUUID();

        when(userService.getUserFavorites(userId.toString()))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/favorites", userId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(String.class).isEqualTo("Unexpected error happened.");

        verify(userService, times(1)).getUserFavorites(userId.toString());
    }

    @Test
    @DisplayName("GET /users/{userId}/bookmarks returns bookmarked challenges")
    void getUserBookmarks_returnsBookmarks() {
        UUID userId = UUID.randomUUID();
        Set<UUID> expectedBookmarks = Set.of(UUID.randomUUID(), UUID.randomUUID());

        when(userService.getUserBookmarks(userId.toString())).thenReturn(Mono.just(expectedBookmarks));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/bookmarks", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UUID.class)
                .hasSize(expectedBookmarks.size())
                .contains(expectedBookmarks.toArray(new UUID[0]));

        verify(userService, times(1)).getUserBookmarks(userId.toString());
    }

    @Test
    @DisplayName("GET /users/{userId}/bookmarks returns 404 if user not found")
    void getUserBookmarks_returns404IfUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userService.getUserBookmarks(userId.toString()))
                .thenReturn(Mono.error(new NotFoundException("User not found")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/bookmarks", userId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).isEqualTo("User not found");

        verify(userService, times(1)).getUserBookmarks(userId.toString());
    }

    @Test
    @DisplayName("GET /users/{userId}/bookmarks returns 400 if UUID is invalid")
    void getUserBookmarks_returns400IfInvalidUUID() {
        String invalidUserId = "invalid-uuid";

        when(userService.getUserBookmarks(invalidUserId))
                .thenReturn(Mono.error(new BadUUIDException("The provided IDs are not valid.")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/bookmarks", invalidUserId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class).isEqualTo("The provided IDs are not valid.");

        verify(userService, times(1)).getUserBookmarks(invalidUserId);
    }

    @Test
    @DisplayName("GET /users/{userId}/bookmarks returns 500 if there is an internal error")
    void getUserBookmarks_returns500IfUnexpectedError() {
        UUID userId = UUID.randomUUID();

        when(userService.getUserBookmarks(userId.toString()))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/bookmarks", userId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(String.class).isEqualTo("Unexpected error happened.");

        verify(userService, times(1)).getUserBookmarks(userId.toString());
    }
    
    @Test
    @DisplayName("GET /users/{userId}/solutions returns solutions")
    void getAllSolutions_returnsSolutions() {
        String userId = UUID.randomUUID().toString();
        
        UserSolutionResponseDto sol1 = UserSolutionResponseDto.builder()
                .userId(userId)
                .challengeId("d43a1a4d-ee8f-432d-8f9c-68eda2547dae")
                .languageId("409c9fe8-74de-4db3-81a1-a55280cf92ef")
                .solutionText("This is the submitted solution")
                .build();
        
        UserSolutionResponseDto sol2 = UserSolutionResponseDto.builder()
                .userId(userId)
                .challengeId("b5c06903-f27b-4057-8220-ad9d957cdce4")
                .languageId("09fabe32-7362-4bfb-ac05-b7bf854c6e0f")
                .solutionText("This is the submitted solution")
                .build();
        
        when(userSolutionService.getAllSolutionsByUser(userId))
                .thenReturn(Flux.just(sol1, sol2));
        
        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/solutions", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserSolutionResponseDto.class)
                .hasSize(2)
                .value(list -> {
                    
                    Assertions.assertEquals(sol1.getUserId(), list.get(0).getUserId());
                    Assertions.assertEquals(sol1.getChallengeId(), list.get(0).getChallengeId());
                    Assertions.assertEquals(sol1.getLanguageId(), list.get(0).getLanguageId());
                    Assertions.assertEquals(sol1.getSolutionText(), list.get(0).getSolutionText());
                    
                    Assertions.assertEquals(sol2.getUserId(), list.get(1).getUserId());
                    Assertions.assertEquals(sol2.getChallengeId(), list.get(1).getChallengeId());
                    Assertions.assertEquals(sol2.getLanguageId(), list.get(1).getLanguageId());
                    Assertions.assertEquals(sol2.getSolutionText(), list.get(1).getSolutionText());
                });
        
        verify(userSolutionService, times(1)).getAllSolutionsByUser(userId);
    }
    
    @Test
    @DisplayName("GET /users/{userId}/solutions returns 404 if no solutions found")
    void getAllSolutions_returns404IfNotFound() {
        String userId = UUID.randomUUID().toString();
        
        // Simulamos que el servicio lanza NotFoundException
        when(userSolutionService.getAllSolutionsByUser(userId))
                .thenReturn(Flux.error(new NotFoundException("Solutions not found")));
        
        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/solutions", userId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("Solutions not found");
        
        verify(userSolutionService, times(1)).getAllSolutionsByUser(userId);
    }
    
    @Test
    @DisplayName("GET /users/{userId}/solutions returns 400 if UUID is invalid")
    void getAllSolutions_returns400IfInvalidUUID() {
        String badUserId = "not-a-uuid";
        
        when(userSolutionService.getAllSolutionsByUser(badUserId))
                .thenReturn(Flux.error(new BadUUIDException("Bad UUID")));
        
        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/solutions", badUserId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("The provided IDs are not valid.");
        
        verify(userSolutionService, times(1)).getAllSolutionsByUser(badUserId);
    }
    
    @Test
    @DisplayName("GET /users/{userId}/solutions returns 500 on unexpected error")
    void getAllSolutions_returns500IfUnexpectedError() {
        String userId = UUID.randomUUID().toString();
        
        when(userSolutionService.getAllSolutionsByUser(userId))
                .thenReturn(Flux.error(new RuntimeException("Boom")));
        
        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/{userId}/solutions", userId)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(String.class)
                .isEqualTo("Unexpected error happened.");
        
        verify(userSolutionService, times(1)).getAllSolutionsByUser(userId);
    }

    @Test
    @DisplayName("GET /users/github/{githubUsername}/solutions returns solutions when user exists")
    void getSolutionsByGithubUsername_WhenUserExists_Returns200() {
        String githubUsername = "existingUser";
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, githubUsername, Role.USER, null, null);

        UserSolutionResponseDto solution1 = UserSolutionResponseDto.builder()
                .userId(userId.toString())
                .challengeId(UUID.randomUUID().toString())
                .languageId(UUID.randomUUID().toString())
                .solutionText("Solution 1")
                .status("ENDED")
                .build();

        UserSolutionResponseDto solution2 = UserSolutionResponseDto.builder()
                .userId(userId.toString())
                .challengeId(UUID.randomUUID().toString())
                .languageId(UUID.randomUUID().toString())
                .solutionText("Solution 2")
                .status("IN_PROGRESS")
                .build();

        when(userService.getUser(githubUsername)).thenReturn(Mono.just(user));
        when(userSolutionService.getAllSolutionsByUser(userId.toString())).thenReturn(Flux.just(solution1, solution2));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/github/" + githubUsername + "/solutions")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Validation-Status")
                .expectHeader().valueEquals("X-Validation-Status", "Success")
                .expectHeader().valueEquals("X-Github-Username", githubUsername)
                .expectBodyList(UserSolutionResponseDto.class)
                .hasSize(2)
                .value(list -> {
                    Assertions.assertEquals(solution1.getUserId(), list.get(0).getUserId());
                    Assertions.assertEquals(solution1.getChallengeId(), list.get(0).getChallengeId());
                    Assertions.assertEquals(solution1.getLanguageId(), list.get(0).getLanguageId());
                    Assertions.assertEquals(solution1.getSolutionText(), list.get(0).getSolutionText());
                    Assertions.assertEquals(solution1.getStatus(), list.get(0).getStatus());
                    Assertions.assertEquals(solution2.getUserId(), list.get(1).getUserId());
                    Assertions.assertEquals(solution2.getChallengeId(), list.get(1).getChallengeId());
                    Assertions.assertEquals(solution2.getLanguageId(), list.get(1).getLanguageId());
                    Assertions.assertEquals(solution2.getSolutionText(), list.get(1).getSolutionText());
                    Assertions.assertEquals(solution2.getStatus(), list.get(1).getStatus());
                });

        verify(userService, times(1)).getUser(githubUsername);
        verify(userSolutionService, times(1)).getAllSolutionsByUser(userId.toString());
    }

    @Test
    @DisplayName("GET /users/github/{githubUsername}/solutions returns empty list when user has no solutions")
    void getSolutionsByGithubUsername_WhenUserHasNoSolutions_Returns200WithEmptyList() {
        String githubUsername = "userWithNoSolutions";
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, githubUsername, Role.USER, null, null);

        when(userService.getUser(githubUsername)).thenReturn(Mono.just(user));
        when(userSolutionService.getAllSolutionsByUser(userId.toString())).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/github/" + githubUsername + "/solutions")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Validation-Status", "Success")
                .expectHeader().valueEquals("X-Github-Username", githubUsername)
                .expectBodyList(UserSolutionResponseDto.class)
                .hasSize(0);

        verify(userService, times(1)).getUser(githubUsername);
        verify(userSolutionService, times(1)).getAllSolutionsByUser(userId.toString());
    }

    @Test
    @DisplayName("GET /users/github/{githubUsername}/solutions returns 404 when user not found")
    void getSolutionsByGithubUsername_WhenUserNotExists_Returns404() {
        String githubUsername = "nonExistentUser";

        when(userService.getUser(githubUsername))
                .thenReturn(Mono.error(new NotFoundException("User not found with GitHub username: " + githubUsername)));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/github/" + githubUsername + "/solutions")
                .exchange()
                .expectStatus().isNotFound();

        verify(userService, times(1)).getUser(githubUsername);
        verify(userSolutionService, never()).getAllSolutionsByUser(anyString());
    }

    @Test
    @DisplayName("GET /users/github/{githubUsername}/solutions returns 400 when invalid GitHub username")
    void getSolutionsByGithubUsername_WhenInvalidUsername_Returns400() {
        String invalidGithubUsername = "invalid username with spaces";

        when(userService.getUser(invalidGithubUsername))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid GitHub username")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/github/" + invalidGithubUsername + "/solutions")
                .exchange()
                .expectStatus().isNotFound();

        verify(userService, times(1)).getUser(invalidGithubUsername);
        verify(userSolutionService, never()).getAllSolutionsByUser(anyString());
    }

    @Test
    @DisplayName("GET /users/github/{githubUsername}/solutions returns 500 when solution service throws unexpected error")
    void getSolutionsByGithubUsername_WhenSolutionServiceThrowsUnexpectedError_Returns500() {
        String githubUsername = "testuser";
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, githubUsername, Role.USER, null, null);

        when(userService.getUser(githubUsername))
                .thenReturn(Mono.just(user));
        when(userSolutionService.getAllSolutionsByUser(userId.toString()))
                .thenReturn(Flux.error(new RuntimeException("Unexpected error")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/github/" + githubUsername + "/solutions")
                .exchange()
                .expectStatus().is5xxServerError();

        verify(userService, times(1)).getUser(githubUsername);
        verify(userSolutionService, times(1)).getAllSolutionsByUser(userId.toString());
    }

    @Test
    @DisplayName("GET /users/github/{githubUsername}/solutions returns 404 and empty list when solution service throws NotFoundException")
    void getSolutionsByGithubUsername_Returns404AndEmptyList_WhenSolutionServiceThrowsNotFoundException() {
        String githubUsername = "testuser404";
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, githubUsername, Role.USER, null, null);

        when(userService.getUser(githubUsername)).thenReturn(Mono.just(user));
        when(userSolutionService.getAllSolutionsByUser(userId.toString())).thenReturn(Flux.error(new NotFoundException("Solutions not found")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/github/" + githubUsername + "/solutions")
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectHeader().contentType("text/plain;charset=UTF-8");
    }

    @Test
    @DisplayName("GET /users/github/{githubUsername}/solutions returns 400 and empty list when solution service throws IllegalArgumentException")
    void getSolutionsByGithubUsername_Returns400AndEmptyList_WhenSolutionServiceThrowsIllegalArgumentException() {
        String githubUsername = "testuser400";
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, githubUsername, Role.USER, null, null);

        when(userService.getUser(githubUsername)).thenReturn(Mono.just(user));
        when(userSolutionService.getAllSolutionsByUser(userId.toString())).thenReturn(Flux.error(new IllegalArgumentException("Invalid argument")));

        webTestClient.get()
                .uri("/itachallenge/api/v1/user/users/github/" + githubUsername + "/solutions")
                .exchange()
                .expectStatus().isEqualTo(404)
                .expectHeader().contentType("text/plain;charset=UTF-8");

    }
}
