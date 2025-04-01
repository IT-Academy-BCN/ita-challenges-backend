package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

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
        UserDocument existingUser = new UserDocument(UUID.randomUUID(), username, Role.ADMIN, null);
        when(userRepository.findByUsername(username)).thenReturn(Mono.just(existingUser));

        StepVerifier.create(userService.getUser(username))
                .expectNext(existingUser)
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void getUser_ShouldReturnEmptyMono_WhenUserDoesNotExist() {
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Mono.empty());

        StepVerifier.create(userService.getUser(username))
                .expectNextCount(0)
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void addChallengeToFavorites_ShouldReturnTrue_WhenFavoritesIsNull() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, null);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.addChallengeToFavorites(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        assertNotNull(user.getFavoriteChallenges());
        assertTrue(user.getFavoriteChallenges().contains(challengeId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addChallengeToFavorites_ShouldReturnTrue_WhenFavoritesIsEmpty() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, new HashSet<>());

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.addChallengeToFavorites(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        assertNotNull(user.getFavoriteChallenges());
        assertTrue(user.getFavoriteChallenges().contains(challengeId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addChallengeToFavorites_ShouldReturnTrue_WhenFavoritesHasValues() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Set<UUID> favorites = new HashSet<>(Set.of(UUID.randomUUID(), UUID.randomUUID()));
        UserDocument user = new UserDocument(userId, "testUser", null, favorites);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.addChallengeToFavorites(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        assertNotNull(user.getFavoriteChallenges());
        assertTrue(user.getFavoriteChallenges().contains(challengeId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void addChallengeToFavorites_ShouldReturnFalse_WhenFavoritesAlreadyContainsChallenge() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Set<UUID> favorites = new HashSet<>(Set.of(UUID.randomUUID(), UUID.randomUUID(), challengeId));
        UserDocument user = new UserDocument(userId, "testUser", null, favorites);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.addChallengeToFavorites(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        assertNotNull(user.getFavoriteChallenges());
        assertTrue(user.getFavoriteChallenges().contains(challengeId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void addChallengeToFavorites_ShouldThrowNotFoundException_WhenUserNotFound() {

        when(userRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(userService.addChallengeToFavorites(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
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
        StepVerifier.create(userService.addChallengeToFavorites(null, UUID.randomUUID().toString()))
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
        StepVerifier.create(userService.addChallengeToFavorites(UUID.randomUUID().toString(), null))
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
        StepVerifier.create(userService.addChallengeToFavorites("invalidUuid", UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void addChallengeToFavorites_ShouldThrowBadRequestException_WhenChallengeUuidIsNotValid() {
        StepVerifier.create(userService.addChallengeToFavorites(UUID.randomUUID().toString(), "invalidUuid"))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromFavorites_ShouldReturnFalse_WhenFavoritesIsNull() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, null);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.deleteChallengeFromFavorites(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        assertNull(user.getFavoriteChallenges());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void deleteChallengeFromFavorites_ShouldReturnFalse_WhenFavoritesIsEmpty() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, new HashSet<>());

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.deleteChallengeFromFavorites(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        assertNotNull(user.getFavoriteChallenges());
        assertFalse(user.getFavoriteChallenges().contains(challengeId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void deleteChallengeFromFavorites_ShouldReturnFalse_WhenChallengeNotInFavorites() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Set<UUID> favorites = new HashSet<>(Set.of(UUID.randomUUID(), UUID.randomUUID()));
        UserDocument user = new UserDocument(userId, "testUser", null, favorites);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.deleteChallengeFromFavorites(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        assertNotNull(user.getFavoriteChallenges());
        assertFalse(user.getFavoriteChallenges().contains(challengeId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void deleteChallengeFromFavorites_ShouldReturnTrue_WhenFavoritesAlreadyContainsChallenge() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Set<UUID> favorites = new HashSet<>(Set.of(UUID.randomUUID(), UUID.randomUUID(), challengeId));
        UserDocument user = new UserDocument(userId, "testUser", null, favorites);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(userRepository.save(user)).thenReturn(Mono.just(user));

        StepVerifier.create(userService.deleteChallengeFromFavorites(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        assertNotNull(user.getFavoriteChallenges());
        assertFalse(user.getFavoriteChallenges().contains(challengeId));

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
    @DisplayName("getUserFavorites returns favorite challenges when the user exists and has challenges")
    void getUserFavorites_WhenUserExistsWithFavorites_ReturnsSet() {
        UUID userId = UUID.randomUUID();
        UUID challengeId1 = UUID.randomUUID();
        UUID challengeId2 = UUID.randomUUID();

        Set<UUID> favorites = Set.of(challengeId1, challengeId2);
        UserDocument user = new UserDocument();
        user.setUuid(userId);
        user.setFavoriteChallenges(favorites);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));

        userService.getUserFavorites(userId.toString())
                .as(StepVerifier::create)
                .expectNextMatches(result -> result.size() == 2 && result.contains(challengeId1))
                .verifyComplete();
    }

    @Test
    @DisplayName("getUserFavorites returns an empty set when the user has no challenges marked.")
    void getUserFavorites_WhenUserHasNoFavorites_ReturnsEmptySet() {
        UUID userId = UUID.randomUUID();

        UserDocument user = new UserDocument();
        user.setUuid(userId);
        user.setFavoriteChallenges(null); // explícitament null

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));

        userService.getUserFavorites(userId.toString())
                .as(StepVerifier::create)
                .expectNextMatches(Set::isEmpty)
                .verifyComplete();
    }

    @Test
    @DisplayName("getUserFavorites returns NotFoundException error when the user does not exist")
    void getUserFavorites_WhenUserNotFound_ReturnsError() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        userService.getUserFavorites(userId.toString())
                .as(StepVerifier::create)
                .expectErrorMatches(error ->
                        error instanceof NotFoundException &&
                                error.getMessage().equals("User not found."))
                .verify();
    }

    @Test
    @DisplayName("getUserFavorites throws BadUUIDException when the UUID format is invalid")
    void getUserFavorites_WhenInvalidUUID_ReturnsBadUUIDException() {
        String invalidUUID = "invalid-uuid";

        userService.getUserFavorites(invalidUUID)
                .as(StepVerifier::create)
                .expectErrorMatches(error ->
                        error instanceof BadUUIDException &&
                                error.getMessage().equals("Invalid ID format"))
                .verify();
    }

}
