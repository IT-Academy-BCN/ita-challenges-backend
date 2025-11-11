package com.itachallenge.userinteraction.service.favorite;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.userinteraction.document.favorite.FavoriteDocument;
import com.itachallenge.userinteraction.repository.favorite.FavoriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    @BeforeEach
    void setUp() {
        favoriteService = new FavoriteServiceImpl(favoriteRepository);
    }

    @Test
    @DisplayName("Add to favorites when favorite does not exist")
    void addChallengeToFavorites_ShouldReturnTrue_WhenFavoriteDoesNotExist() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        
        when(favoriteRepository.existsByUserIdAndChallengeId(any(UUID.class), any(UUID.class)))
                .thenReturn(Mono.just(false));
        when(favoriteRepository.save(any(FavoriteDocument.class)))
                .thenReturn(Mono.just(new FavoriteDocument()));

        StepVerifier.create(favoriteService.addChallengeToFavorites(userId, challengeId))
                .expectNext(true)
                .verifyComplete();

        verify(favoriteRepository).existsByUserIdAndChallengeId(any(UUID.class), any(UUID.class));
        verify(favoriteRepository).save(any(FavoriteDocument.class));
    }

    @Test
    @DisplayName("Add to favorites when favorite already exists")
    void addChallengeToFavorites_ShouldReturnFalse_WhenFavoriteAlreadyExists() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        
        when(favoriteRepository.existsByUserIdAndChallengeId(any(UUID.class), any(UUID.class)))
                .thenReturn(Mono.just(true));

        StepVerifier.create(favoriteService.addChallengeToFavorites(userId, challengeId))
                .expectNext(false)
                .verifyComplete();

        verify(favoriteRepository).existsByUserIdAndChallengeId(any(UUID.class), any(UUID.class));
        verify(favoriteRepository, never()).save(any(FavoriteDocument.class));
    }


    @Test
    void addChallengeToFavorites_ShouldThrowNotFoundException_WhenUserNotFound() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();

        when(favoriteRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(favoriteService.addChallengeToFavorites(userId.toString(), challengeId.toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(NotFoundException.class, throwable);
                    assertEquals("User not found", throwable.getMessage());
                })
                .verify();

        verify(favoriteRepository, times(1)).findById(userId);
        verify(favoriteRepository, never()).existsByUserIdAndChallengeId(any(), any());
        verify(favoriteRepository, never()).save(any());
        verify(favoriteRepository, never()).save(any());
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

        verify(favoriteRepository, never()).findById(Mockito.<UUID>any());
        verify(favoriteRepository, never()).existsByUserIdAndChallengeId(any(), any());
        verify(favoriteRepository, never()).save(any());
        verify(favoriteRepository, never()).save(any());
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

        verify(favoriteRepository, never()).findById(Mockito.<UUID>any());
        verify(favoriteRepository, never()).existsByUserIdAndChallengeId(any(), any());
        verify(favoriteRepository, never()).save(any());
        verify(favoriteRepository, never()).save(any());
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

        verify(favoriteRepository, never()).findById(Mockito.<UUID>any());
        verify(favoriteRepository, never()).existsByUserIdAndChallengeId(any(), any());
        verify(favoriteRepository, never()).save(any());
        verify(favoriteRepository, never()).save(any());
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

        verify(favoriteRepository, never()).findById(Mockito.<UUID>any());
        verify(favoriteRepository, never()).existsByUserIdAndChallengeId(any(), any());
        verify(favoriteRepository, never()).save(any());
        verify(favoriteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Delete from favorites when favorite exists")
    void deleteChallengeFromFavorites_ShouldReturnTrue_WhenFavoriteExists() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        FavoriteDocument favorite = new FavoriteDocument(UUID.randomUUID(), UUID.fromString(userId), 
                UUID.fromString(challengeId), LocalDateTime.now());
        
        when(favoriteRepository.findByUserIdAndChallengeId(any(UUID.class), any(UUID.class)))
                .thenReturn(Mono.just(favorite));
        when(favoriteRepository.delete(any(FavoriteDocument.class))).thenReturn(Mono.empty());

        StepVerifier.create(favoriteService.deleteChallengeFromFavorites(userId, challengeId))
                .expectNext(true)
                .verifyComplete();

        verify(favoriteRepository).findByUserIdAndChallengeId(any(UUID.class), any(UUID.class));
        verify(favoriteRepository).delete(any(FavoriteDocument.class));
    }

    @Test
    @DisplayName("Delete from favorites when favorite does not exist")
    void deleteChallengeFromFavorites_ShouldReturnFalse_WhenFavoriteDoesNotExist() {
        String userId = UUID.randomUUID().toString();
        String challengeId = UUID.randomUUID().toString();
        
        when(favoriteRepository.findByUserIdAndChallengeId(any(UUID.class), any(UUID.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(favoriteService.deleteChallengeFromFavorites(userId, challengeId))
                .expectNext(false)
                .verifyComplete();

        verify(favoriteRepository).findByUserIdAndChallengeId(any(UUID.class), any(UUID.class));
        verify(favoriteRepository, never()).delete(any(FavoriteDocument.class));
    }

    @Test
    void deleteChallengeFromFavorites_ShouldThrowNotFoundException_WhenUserNotFound() {

        when(favoriteRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(favoriteService.deleteChallengeFromFavorites(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(NotFoundException.class, throwable);
                    assertEquals("User not found", throwable.getMessage());
                })
                .verify();

        verify(favoriteRepository, times(1)).findById(any(UUID.class));
        verify(favoriteRepository, times(0)).save(any());
    }


    @Test
    void deleteChallengeFromFavorites_ShouldThrowBadRequestException_WhenUserUuidIsNull() {
        StepVerifier.create(favoriteService.deleteChallengeFromFavorites(null, UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(favoriteRepository, times(0)).findById(any(UUID.class));
        verify(favoriteRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromFavorites_ShouldThrowBadRequestException_WhenChallengeUuidIsNull() {
        StepVerifier.create(favoriteService.deleteChallengeFromFavorites(UUID.randomUUID().toString(), null))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(favoriteRepository, times(0)).findById(any(UUID.class));
        verify(favoriteRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromFavorites_ShouldThrowBadRequestException_WhenUserUuidIsNotValid() {
        StepVerifier.create(favoriteService.deleteChallengeFromFavorites("invalidUuid", UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(favoriteRepository, times(0)).findById(any(UUID.class));
        verify(favoriteRepository, times(0)).save(any());
    }


    @Test
    void deleteChallengeFromFavorites_ShouldThrowBadRequestException_WhenChallengeUuidIsNotValid() {
        StepVerifier.create(favoriteService.deleteChallengeFromFavorites(UUID.randomUUID().toString(), "invalidUuid"))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(favoriteRepository, times(0)).findById(any(UUID.class));
        verify(favoriteRepository, times(0)).save(any());
    }


    @Test
    @DisplayName("getUserFavorites returns an empty set when the user has no challenges marked.")
    void getUserFavorites_WhenUserHasNoFavorites_ReturnsEmptySet() {
        UUID userId = UUID.randomUUID();

        UserDocument user = new UserDocument();
        user.setUuid(userId);
        user.setFavoriteChallenges(null);

        when(favoriteRepository.findById(userId)).thenReturn(Mono.just(user));

        favoriteService.getUserFavorites(userId.toString())
                .as(StepVerifier::create)
                .expectNextMatches(Set::isEmpty)
                .verifyComplete();
    }


    @Test
    @DisplayName("getUserFavorites returns NotFoundException error when the user does not exist")
    void getUserFavorites_WhenUserNotFound_ReturnsError() {
        UUID userId = UUID.randomUUID();

        when(favoriteRepository.findById(userId)).thenReturn(Mono.empty());

        favoriteService.getUserFavorites(userId.toString())
                .as(StepVerifier::create)
                .expectErrorMatches(error ->
                        error instanceof NotFoundException &&
                                error.getMessage().equals("User not found with id: " + userId))
                .verify();
    }

    @Test
    @DisplayName("getUserFavorites throws BadUUIDException when the UUID format is invalid")
    void getUserFavorites_WhenInvalidUUID_ReturnsBadUUIDException() {
        String invalidUUID = "invalid-uuid";

        favoriteService.getUserFavorites(invalidUUID)
                .as(StepVerifier::create)
                .expectErrorMatches(error ->
                        error instanceof BadUUIDException &&
                                error.getMessage().equals("Invalid ID format"))
                .verify();
    }
}
