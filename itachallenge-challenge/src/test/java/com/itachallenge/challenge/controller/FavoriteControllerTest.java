package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.dto.FavoriteDto;
import com.itachallenge.common.exception.BadRequestException;
import com.itachallenge.challenge.exception.ChallengeNotFoundException;
import com.itachallenge.challenge.exception.JwtException;
import com.itachallenge.challenge.exception.InternalServerErrorException;
import com.itachallenge.challenge.service.IFavoriteService;
import com.itachallenge.challenge.service.IChallengeJwtFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

public class FavoriteControllerTest {

    private IFavoriteService favoriteService;
    private IChallengeJwtFacade challengeJwtFacade;
    private FavoriteController favoriteController;

    @BeforeEach
    void setUp() {
        favoriteService = mock(IFavoriteService.class);
        challengeJwtFacade = mock(IChallengeJwtFacade.class);
        favoriteController = new FavoriteController(favoriteService, challengeJwtFacade);
    }

    @Test
    void addFavorite_success_200() {
        String challengeId = "123e4567-e89b-12d3-a456-426614174000";
        String userId = "321e4567-e89b-12d3-a456-426614174000";
        String authHeader = "Bearer token";
        FavoriteDto dto = new FavoriteDto(true, 1);

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);
        when(favoriteService.addChallengeToFavorites(challengeId, userId)).thenReturn(Mono.just(dto));

        Mono<ResponseEntity<FavoriteDto>> result = favoriteController.addFavorite(challengeId, authHeader);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getStatusCode().is2xxSuccessful() &&
                        response.getBody().isFavorite() && response.getBody().getTimesFavorited() == 1)
                .verifyComplete();
    }

    @Test
    void addFavorite_missingAuthHeader_400() {
        String challengeId = "123e4567-e89b-12d3-a456-426614174000";
        String authHeader = null;
        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader)).thenThrow(new JwtException("Missing header"));

        Mono<ResponseEntity<FavoriteDto>> result = favoriteController.addFavorite(challengeId, authHeader);

        StepVerifier.create(result)
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void addFavorite_invalidAuthHeader_400() {
        String challengeId = "123e4567-e89b-12d3-a456-426614174000";
        String authHeader = "invalid";
        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader)).thenThrow(new JwtException("Invalid header"));

        Mono<ResponseEntity<FavoriteDto>> result = favoriteController.addFavorite(challengeId, authHeader);

        StepVerifier.create(result)
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void addFavorite_challengeNotFound_404() {
        String challengeId = "not-found-id";
        String userId = "321e4567-e89b-12d3-a456-426614174000";
        String authHeader = "Bearer token";

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);
        when(favoriteService.addChallengeToFavorites(challengeId, userId))
                .thenReturn(Mono.error(new ChallengeNotFoundException("Challenge not found")));

        Mono<ResponseEntity<FavoriteDto>> result = favoriteController.addFavorite(challengeId, authHeader);

        StepVerifier.create(result)
                .expectError(ChallengeNotFoundException.class)
                .verify();
    }

    @Test
    void addFavorite_internalServerError_500() {
        String challengeId = "123e4567-e89b-12d3-a456-426614174000";
        String userId = "321e4567-e89b-12d3-a456-426614174000";
        String authHeader = "Bearer token";

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);
        when(favoriteService.addChallengeToFavorites(challengeId, userId))
                .thenReturn(Mono.error(new InternalServerErrorException("Internal error")));

        Mono<ResponseEntity<FavoriteDto>> result = favoriteController.addFavorite(challengeId, authHeader);

        StepVerifier.create(result)
                .expectError(InternalServerErrorException.class)
                .verify();
    }

    @Test
    void removeFavorite_success_200() {
        String challengeId = "123e4567-e89b-12d3-a456-426614174000";
        String userId = "321e4567-e89b-12d3-a456-426614174000";
        String authHeader = "Bearer token";
        FavoriteDto dto = new FavoriteDto(false, 0);

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);
        when(favoriteService.removeChallengeFromFavorites(challengeId, userId)).thenReturn(Mono.just(dto));

        Mono<ResponseEntity<FavoriteDto>> result = favoriteController.removeFavorite(userId, challengeId, authHeader);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getStatusCode().is2xxSuccessful() &&
                        !response.getBody().isFavorite() && response.getBody().getTimesFavorited() == 0)
                .verifyComplete();
    }

    @Test
    void removeFavorite_missingAuthHeader_400() {
        String challengeId = "123e4567-e89b-12d3-a456-426614174000";
        String authHeader = null;
        String userId = "user-123";
        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader)).thenThrow(new JwtException("Missing header"));

        Mono<ResponseEntity<FavoriteDto>> result = favoriteController.removeFavorite(userId, challengeId, authHeader);

        StepVerifier.create(result)
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void removeFavorite_challengeNotFound_404() {
        String challengeId = "not-found-id";
        String userId = "321e4567-e89b-12d3-a456-426614174000";
        String authHeader = "Bearer token";

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);
        when(favoriteService.removeChallengeFromFavorites(challengeId, userId))
                .thenReturn(Mono.error(new ChallengeNotFoundException("Challenge not found")));

        Mono<ResponseEntity<FavoriteDto>> result = favoriteController.removeFavorite(userId, challengeId, authHeader);

        StepVerifier.create(result)
                .expectError(ChallengeNotFoundException.class)
                .verify();
    }

    @Test
    void removeFavorite_internalServerError_500() {
        String challengeId = "123e4567-e89b-12d3-a456-426614174000";
        String userId = "321e4567-e89b-12d3-a456-426614174000";
        String authHeader = "Bearer token";

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);
        when(favoriteService.removeChallengeFromFavorites(challengeId, userId))
                .thenReturn(Mono.error(new InternalServerErrorException("Internal error")));

        Mono<ResponseEntity<FavoriteDto>> result = favoriteController.removeFavorite(userId, challengeId, authHeader);

        StepVerifier.create(result)
                .expectError(InternalServerErrorException.class)
                .verify();
    }

    @Test
    void removeFavorite_userIdMismatch_400() {
        String challengeId = "123e4567-e89b-12d3-a456-426614174000";
        String userIdInPath = "user-abc";
        String userIdInToken = "user-diff";
        String authHeader = "Bearer valid-token";

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userIdInToken);

        Mono<ResponseEntity<FavoriteDto>> result = favoriteController.removeFavorite(userIdInPath, challengeId, authHeader);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof BadRequestException &&
                        throwable.getMessage().contains("You cannot remove favorites for another user"))
                .verify();
    }

    @Test
    @SuppressWarnings("deprecation")
    @DisplayName("DELETE Legacy /remove returns 200 with Deprecation headers")
    void removeFavoriteLegacy_Success_ReturnsHeaders() {
        String challengeId = "challenge-789";
        String userId = "user-123";
        String authHeader = "Bearer valid-token";
        FavoriteDto mockDto = new FavoriteDto(false, 0);

        when(challengeJwtFacade.getUserUuIdFromAuthenticationHeader(authHeader))
                .thenReturn(userId);
        when(favoriteService.removeChallengeFromFavorites(challengeId, userId))
                .thenReturn(Mono.just(mockDto));

        Mono<ResponseEntity<FavoriteDto>> result = favoriteController.removeFavoriteLegacy(challengeId, authHeader);

        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.getStatusCode().is2xxSuccessful() &&
                                "true".equals(response.getHeaders().getFirst("Deprecation")) &&
                                response.getHeaders().containsKey("Link") &&
                                response.getBody().equals(mockDto))
                .verifyComplete();

        verify(favoriteService).removeChallengeFromFavorites(challengeId, userId);
    }
}