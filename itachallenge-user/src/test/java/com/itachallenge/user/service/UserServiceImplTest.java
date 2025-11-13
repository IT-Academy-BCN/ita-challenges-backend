package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.repository.UserRepository;
import com.itachallenge.userinteraction.document.favorite.FavoriteDocument;
import com.itachallenge.userinteraction.repository.favorite.FavoriteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    void getUser_ShouldReturnUser_WhenUserExists() {
        String username = "existingUser";
        UserDocument existingUser = new UserDocument(UUID.randomUUID(), username, Role.ADMIN, null, 0);
        when(userRepository.findByUsername(username)).thenReturn(Mono.just(existingUser));

        StepVerifier.create(userService.getUser(username))
                .expectNext(existingUser)
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void getUser_ShouldReturnNotFoundException_WhenUserDoesNotExist() {
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Mono.empty());

        StepVerifier.create(userService.getUser(username))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(NotFoundException.class, error);
                    assertEquals("User not found", error.getMessage());
                })
                .verify();

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void addChallengeToFavorites_ShouldReturnTrue_WhenFavoriteDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(favoriteRepository.existsByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(false));
        when(favoriteRepository.save(any(FavoriteDocument.class)))
                .thenReturn(Mono.just(new FavoriteDocument()));

        StepVerifier.create(userService.addChallengeToFavorites(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(favoriteRepository, times(1)).existsByUserIdAndChallengeId(userId, challengeId);
        verify(favoriteRepository, times(1)).save(any(FavoriteDocument.class));
        verify(userRepository, never()).save(any());
    }

    @Test
    void addChallengeToBookmarks_ShouldReturnTrue_WhenBookmarksIsNull() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.addChallengeToBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        assertNotNull(user.getBookmarkChallenges());
        assertTrue(user.getBookmarkChallenges().contains(challengeId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addChallengeToBookmarks_ShouldReturnTrue_WhenBookmarksIsEmpty() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, new HashSet<>(), 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.addChallengeToBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        assertNotNull(user.getBookmarkChallenges());
        assertTrue(user.getBookmarkChallenges().contains(challengeId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addChallengeToBookmarks_ShouldReturnTrue_WhenBookmarksHasValues() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Set<UUID> bookmarks = new HashSet<>(Set.of(UUID.randomUUID(), UUID.randomUUID()));
        UserDocument user = new UserDocument(userId, "testUser", null, bookmarks, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.addChallengeToBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        assertNotNull(user.getBookmarkChallenges());
        assertTrue(user.getBookmarkChallenges().contains(challengeId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addChallengeToFavorites_ShouldReturnFalse_WhenFavoriteAlreadyExists() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null,null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(favoriteRepository.existsByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(true));

        StepVerifier.create(userService.addChallengeToFavorites(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(favoriteRepository, times(1)).existsByUserIdAndChallengeId(userId, challengeId);
        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void addChallengeToBookmarks_ShouldReturnFalse_WhenBookmarksAlreadyContainsChallenge() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Set<UUID> bookmarks = new HashSet<>(Set.of(UUID.randomUUID(), UUID.randomUUID(), challengeId));
        UserDocument user = new UserDocument(userId, "testUser", null, bookmarks, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.addChallengeToBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        assertNotNull(user.getBookmarkChallenges());
        assertTrue(user.getBookmarkChallenges().contains(challengeId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void addChallengeToFavorites_ShouldThrowNotFoundException_WhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        when(userRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(userService.addChallengeToFavorites(userId.toString(), challengeId.toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(NotFoundException.class, throwable);
                    assertEquals("User not found", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(1)).findById(userId);
        verify(favoriteRepository, never()).existsByUserIdAndChallengeId(any(), any());
        verify(favoriteRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void addChallengeToBookmarks_ShouldThrowNotFoundException_WhenUserNotFound() {

        when(userRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(userService.addChallengeToBookmarks(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(NotFoundException.class, throwable);
                    assertEquals("User not found", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void addChallengeToFavorites_ShouldThrowBadRequestException_WhenUserUuidIsNull() {
        String invalidUserId = null;
        String validChallengeId = UUID.randomUUID().toString();

        StepVerifier.create(userService.addChallengeToFavorites(invalidUserId, validChallengeId))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, never()).findById(Mockito.<UUID>any());
        verify(favoriteRepository, never()).existsByUserIdAndChallengeId(any(), any());
        verify(favoriteRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void addChallengeToBookmarks_ShouldThrowBadRequestException_WhenUserUuidIsNull() {
        StepVerifier.create(userService.addChallengeToBookmarks(null, UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void addChallengeToFavorites_ShouldThrowBadRequestException_WhenChallengeUuidIsNull() {
        String validUserId = UUID.randomUUID().toString();
        String invalidChallengeId = null;

        StepVerifier.create(userService.addChallengeToFavorites(validUserId, invalidChallengeId))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, never()).findById(Mockito.<UUID>any());
        verify(favoriteRepository, never()).existsByUserIdAndChallengeId(any(), any());
        verify(favoriteRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void addChallengeToBookmarks_ShouldThrowBadRequestException_WhenChallengeUuidIsNull() {
        StepVerifier.create(userService.addChallengeToBookmarks(UUID.randomUUID().toString(), null))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void addChallengeToFavorites_ShouldThrowBadRequestException_WhenUserUuidIsNotValid() {
        String invalidUserId = "invalidUuid";
        String validChallengeId = UUID.randomUUID().toString();

        StepVerifier.create(userService.addChallengeToFavorites(invalidUserId, validChallengeId))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, never()).findById(Mockito.<UUID>any());
        verify(favoriteRepository, never()).existsByUserIdAndChallengeId(any(), any());
        verify(favoriteRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void addChallengeToBookmarks_ShouldThrowBadRequestException_WhenUserUuidIsNotValid() {
        StepVerifier.create(userService.addChallengeToBookmarks("invalidUuid", UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void addChallengeToFavorites_ShouldThrowBadUUIDException_WhenChallengeUuidIsNotValid() {
        String validUserId = UUID.randomUUID().toString();
        String invalidChallengeId = "invalidUuid";

        StepVerifier.create(userService.addChallengeToFavorites(validUserId, invalidChallengeId))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, never()).findById(Mockito.<UUID>any());
        verify(favoriteRepository, never()).existsByUserIdAndChallengeId(any(), any());
        verify(favoriteRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void addChallengeToBookmarks_ShouldThrowBadRequestException_WhenChallengeUuidIsNotValid() {
        StepVerifier.create(userService.addChallengeToBookmarks(UUID.randomUUID().toString(), "invalidUuid"))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromFavorites_ShouldReturnFalse_WhenFavoriteDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(favoriteRepository.findByUserIdAndChallengeId(any(UUID.class), any(UUID.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteChallengeFromFavorites(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        verify(userRepository).findById(userId);
        verify(favoriteRepository).findByUserIdAndChallengeId(any(UUID.class), any(UUID.class));
        verify(favoriteRepository, never()).delete(any());
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldReturnFalse_WhenBookmarksIsNull() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.deleteChallengeFromBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        assertNull(user.getBookmarkChallenges());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldReturnFalse_WhenBookmarksIsEmpty() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, new HashSet<>(), 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.deleteChallengeFromBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        assertNotNull(user.getBookmarkChallenges());
        assertFalse(user.getBookmarkChallenges().contains(challengeId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldReturnFalse_WhenChallengeNotInBookmarks() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Set<UUID> bookmarks = new HashSet<>(Set.of(UUID.randomUUID(), UUID.randomUUID()));
        UserDocument user = new UserDocument(userId, "testUser", null, bookmarks, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.deleteChallengeFromBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        assertNotNull(user.getBookmarkChallenges());
        assertFalse(user.getBookmarkChallenges().contains(challengeId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void deleteChallengeFromFavorites_ShouldReturnTrue_WhenFavoriteAlreadyExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, null, 0);
        FavoriteDocument favorite = new FavoriteDocument(UUID.randomUUID(), userId, challengeId, LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(favoriteRepository.findByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(favorite));
        when(favoriteRepository.delete(any(FavoriteDocument.class))).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(userService.deleteChallengeFromFavorites(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        // Verify
        verify(userRepository).findById(userId);
        verify(favoriteRepository).findByUserIdAndChallengeId(userId, challengeId);
        verify(favoriteRepository).delete(favorite);
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldReturnTrue_WhenBookmarksAlreadyContainsChallenge() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Set<UUID> bookmarks = new HashSet<>(Set.of(UUID.randomUUID(), UUID.randomUUID(), challengeId));
        UserDocument user = new UserDocument(userId, "testUser", null, bookmarks, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.deleteChallengeFromBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        assertNotNull(user.getBookmarkChallenges());
        assertFalse(user.getBookmarkChallenges().contains(challengeId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void deleteChallengeFromFavorites_ShouldThrowNotFoundException_WhenUserNotFound() {

        when(userRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteChallengeFromFavorites(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(NotFoundException.class, throwable);
                    assertEquals("User not found", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldThrowNotFoundException_WhenUserNotFound() {

        when(userRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteChallengeFromBookmarks(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(NotFoundException.class, throwable);
                    assertEquals("User not found", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromFavorites_ShouldThrowBadRequestException_WhenUserUuidIsNull() {
        StepVerifier.create(userService.deleteChallengeFromFavorites(null, UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldThrowBadRequestException_WhenUserUuidIsNull() {
        StepVerifier.create(userService.deleteChallengeFromBookmarks(null, UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromFavorites_ShouldThrowBadRequestException_WhenChallengeUuidIsNull() {
        StepVerifier.create(userService.deleteChallengeFromFavorites(UUID.randomUUID().toString(), null))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldThrowBadRequestException_WhenChallengeUuidIsNull() {
        StepVerifier.create(userService.deleteChallengeFromBookmarks(UUID.randomUUID().toString(), null))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromFavorites_ShouldThrowBadRequestException_WhenUserUuidIsNotValid() {
        StepVerifier.create(userService.deleteChallengeFromFavorites("invalidUuid", UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldThrowBadRequestException_WhenUserUuidIsNotValid() {
        StepVerifier.create(userService.deleteChallengeFromBookmarks("invalidUuid", UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromFavorites_ShouldThrowBadRequestException_WhenChallengeUuidIsNotValid() {
        StepVerifier.create(userService.deleteChallengeFromFavorites(UUID.randomUUID().toString(), "invalidUuid"))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldThrowBadRequestException_WhenChallengeUuidIsNotValid() {
        StepVerifier.create(userService.deleteChallengeFromBookmarks(UUID.randomUUID().toString(), "invalidUuid"))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("getUserBookmarks returns bookmarked challenges when the user exists and has challenges")
    void getUserBookmarks_WhenUserExistsWithBookmarks_ReturnsSet() {
        UUID userId = UUID.randomUUID();
        UUID challengeId1 = UUID.randomUUID();
        UUID challengeId2 = UUID.randomUUID();

        Set<UUID> bookmarks = Set.of(challengeId1, challengeId2);
        UserDocument user = new UserDocument();
        user.setUuid(userId);
        user.setBookmarkChallenges(bookmarks);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));

        userService.getUserBookmarks(userId.toString())
                .as(StepVerifier::create)
                .expectNextMatches(result -> result.size() == 2 && result.contains(challengeId1))
                .verifyComplete();
    }

    @Test
    @DisplayName("getUserBookmarks returns an empty set when the user has no challenges marked.")
    void getUserBookmarks_WhenUserHasNoBookmarks_ReturnsEmptySet() {
        UUID userId = UUID.randomUUID();

        UserDocument user = new UserDocument();
        user.setUuid(userId);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));

        userService.getUserBookmarks(userId.toString())
                .as(StepVerifier::create)
                .expectNextMatches(Set::isEmpty)
                .verifyComplete();
    }

    @Test
    @DisplayName("getUserBookmarks returns NotFoundException error when the user does not exist")
    void getUserBookmarks_WhenUserNotFound_ReturnsError() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        userService.getUserBookmarks(userId.toString())
                .as(StepVerifier::create)
                .expectErrorMatches(error ->
                        error instanceof NotFoundException &&
                                error.getMessage().equals("User not found with id: " + userId))
                .verify();
    }

    @Test
    @DisplayName("getUserBookmarks throws BadUUIDException when the UUID format is invalid")
    void getUserBookmarks_WhenInvalidUUID_ReturnsBadUUIDException() {
        String invalidUUID = "invalid-uuid";

        userService.getUserBookmarks(invalidUUID)
                .as(StepVerifier::create)
                .expectErrorMatches(error ->
                        error instanceof BadUUIDException &&
                                error.getMessage().equals("Invalid ID format"))
                .verify();
    }
    
    @Test
    @DisplayName("getUserById returns the user when the user exists")
    void getUserById_ShouldReturnUser_WhenUserExists() {
        String username = "existingUser";
        UUID userId=UUID.randomUUID();
        UserDocument existingUser = new UserDocument(userId, username, Role.ADMIN, null, 0);
        when(userRepository.findById(userId)).thenReturn(Mono.just(existingUser));
        
        StepVerifier.create(userService.getUserById(userId.toString()))
                .expectNext(existingUser)
                .verifyComplete();
        
        verify(userRepository, times(1)).findById(userId);
    }
    
    @Test
    @DisplayName("getUserById throws NotFoundException when the user does not exist")
    void getUserById_ShouldReturnNotFoundException_WhenUserDoesNotExist() {
        UUID userId=UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Mono.empty());
        
        StepVerifier.create(userService.getUserById(userId.toString()))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(NotFoundException.class, error);
                    assertEquals("User not found", error.getMessage());
                })
                .verify();
        verify(userRepository, times(1)).findById(userId);
    }
}
