package com.itachallenge.userinteraction.service.favorite;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.common.exception.NotFoundException;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

class FavoriteServiceImplTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private AutoCloseable mocks;

    private static final String USER_NOT_FOUND_WITH_ID = "User not found with id: ";

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        favoriteService = new FavoriteServiceImpl(favoriteRepository, userRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) mocks.close();
    }

    @Test
    void addChallengeToFavorites_ShouldReturnTrue_WhenFavoriteDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(favoriteRepository.existsByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(false));
        when(favoriteRepository.save(any(FavoriteDocument.class)))
                .thenReturn(Mono.just(new FavoriteDocument()));

        StepVerifier.create(favoriteService.addChallengeToFavorites(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(favoriteRepository, times(1)).existsByUserIdAndChallengeId(userId, challengeId);
        verify(favoriteRepository, times(1)).save(any(FavoriteDocument.class));
        verify(userRepository, never()).save(any());
    }

    @Test
    void addChallengeToFavorites_ShouldReturnFalse_WhenFavoriteAlreadyExists() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(favoriteRepository.existsByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(true));

        StepVerifier.create(favoriteService.addChallengeToFavorites(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(favoriteRepository, times(1)).existsByUserIdAndChallengeId(userId, challengeId);
        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void addChallengeToFavorites_ShouldThrowNotFoundException_WhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        when(userRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(favoriteService.addChallengeToFavorites(userId.toString(), challengeId.toString()))
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
    void addChallengeToFavorites_ShouldThrowBadRequestException_WhenUserUuidIsNull() {
        String invalidUserId = null;
        String validChallengeId = UUID.randomUUID().toString();

        StepVerifier.create(favoriteService.addChallengeToFavorites(invalidUserId, validChallengeId))
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
    void addChallengeToFavorites_ShouldThrowBadRequestException_WhenChallengeUuidIsNull() {
        String validUserId = UUID.randomUUID().toString();
        String invalidChallengeId = null;

        StepVerifier.create(favoriteService.addChallengeToFavorites(validUserId, invalidChallengeId))
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
    void addChallengeToFavorites_ShouldThrowBadRequestException_WhenUserUuidIsNotValid() {
        String invalidUserId = "invalidUuid";
        String validChallengeId = UUID.randomUUID().toString();

        StepVerifier.create(favoriteService.addChallengeToFavorites(invalidUserId, validChallengeId))
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
    void addChallengeToFavorites_ShouldThrowBadUUIDException_WhenChallengeUuidIsNotValid() {
        String validUserId = UUID.randomUUID().toString();
        String invalidChallengeId = "invalidUuid";

        StepVerifier.create(favoriteService.addChallengeToFavorites(validUserId, invalidChallengeId))
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
    @DisplayName("getUserFavorites returns an empty set when the user exists but has no favorites")
    void getUserFavorites_WhenUserHasNoFavorites_ReturnsEmptySet() {
        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(Mono.just(true));
        when(favoriteRepository.findByUserId(userId)).thenReturn(Flux.empty());

        StepVerifier.create(favoriteService.getUserFavorites(userId.toString()))
                .expectNextMatches(Set::isEmpty)
                .verifyComplete();

        verify(userRepository, times(1)).existsById(userId);
        verify(favoriteRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("getUserFavorites returns favorites when the user exists")
    void getUserFavorites_WhenUserExistsWithFavorites_ReturnsFavorites() {
        UUID userId = UUID.randomUUID();
        UUID challengeId1 = UUID.randomUUID();
        UUID challengeId2 = UUID.randomUUID();

        FavoriteDocument fav1 = FavoriteDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId1)
                .build();
        FavoriteDocument fav2 = FavoriteDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId2)
                .build();

        when(userRepository.existsById(userId)).thenReturn(Mono.just(true));
        when(favoriteRepository.findByUserId(userId)).thenReturn(Flux.just(fav1, fav2));

        StepVerifier.create(favoriteService.getUserFavorites(userId.toString()))
                .expectNextMatches(favorites -> favorites.contains(challengeId1) && favorites.contains(challengeId2))
                .verifyComplete();

        verify(userRepository, times(1)).existsById(userId);
        verify(favoriteRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("getUserFavorites throws NotFoundException when the user does not exist")
    void getUserFavorites_WhenUserNotFound_ThrowsNotFoundException() {
        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(Mono.just(false));

        StepVerifier.create(favoriteService.getUserFavorites(userId.toString()))
                .expectErrorMatches(error ->
                        error instanceof NotFoundException &&
                                error.getMessage().equals(USER_NOT_FOUND_WITH_ID + userId))
                .verify();

        verify(userRepository, times(1)).existsById(userId);
        verify(favoriteRepository, times(0)).findByUserId(userId);
    }

    @Test
    @DisplayName("getUserFavorites throws BadUUIDException when the UUID format is invalid")
    void getUserFavorites_WhenInvalidUUID_ThrowsBadUUIDException() {
        String invalidUUID = "invalid-uuid";

        StepVerifier.create(favoriteService.getUserFavorites(invalidUUID))
                .expectErrorMatches(error ->
                        error instanceof BadUUIDException &&
                                error.getMessage().equals("Invalid ID format"))
                .verify();

        verify(userRepository, times(0)).existsById((UUID) any());
        verify(favoriteRepository, times(0)).findByUserId(any());
    }

    @Test
    @DisplayName("getUserFavorites propagates repository errors correctly")
    void getUserFavorites_WhenRepositoryError_PropagatesError() {
        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(Mono.just(true));
        when(favoriteRepository.findByUserId(userId)).thenReturn(Flux.error(new RuntimeException("DB error")));

        StepVerifier.create(favoriteService.getUserFavorites(userId.toString()))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("DB error"))
                .verify();

        verify(userRepository, times(1)).existsById(userId);
        verify(favoriteRepository, times(1)).findByUserId(userId);
    }

    @Test
    void deleteChallengeFromFavorites_ShouldThrowNotFoundException_WhenUserNotFound() {

        when(userRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(favoriteService.deleteChallengeFromFavorites(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
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
        StepVerifier.create(favoriteService.deleteChallengeFromFavorites(null, UUID.randomUUID().toString()))
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
        StepVerifier.create(favoriteService.deleteChallengeFromFavorites(UUID.randomUUID().toString(), null))
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
        StepVerifier.create(favoriteService.deleteChallengeFromFavorites("invalidUuid", UUID.randomUUID().toString()))
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
        StepVerifier.create(favoriteService.deleteChallengeFromFavorites(UUID.randomUUID().toString(), "invalidUuid"))
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
        UserDocument user = new UserDocument(userId, "testUser", null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(favoriteRepository.findByUserIdAndChallengeId(any(UUID.class), any(UUID.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(favoriteService.deleteChallengeFromFavorites(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        verify(userRepository).findById(userId);
        verify(favoriteRepository).findByUserIdAndChallengeId(any(UUID.class), any(UUID.class));
        verify(favoriteRepository, never()).delete(any());
    }

    @Test
    void deleteChallengeFromFavorites_ShouldReturnTrue_WhenFavoriteAlreadyExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, 0);
        FavoriteDocument favorite = FavoriteDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(favoriteRepository.findByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(favorite));
        when(favoriteRepository.delete(any(FavoriteDocument.class))).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(favoriteService.deleteChallengeFromFavorites(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        // Verify
        verify(userRepository).findById(userId);
        verify(favoriteRepository).findByUserIdAndChallengeId(userId, challengeId);
        verify(favoriteRepository).delete(favorite);
    }
}
