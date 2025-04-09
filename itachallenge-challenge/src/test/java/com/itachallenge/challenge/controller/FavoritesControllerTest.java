package com.itachallenge.challenge.controller;

import com.itachallenge.challenge.config.PropertiesConfig;
import com.itachallenge.challenge.dto.FavoriteDto;
import com.itachallenge.challenge.dto.MessageDto;
import com.itachallenge.challenge.exception.ChallengeNotFoundReturn404Exception;
import com.itachallenge.challenge.exception.InternalServerErrorException;
import com.itachallenge.challenge.exception.JwtException;
import com.itachallenge.challenge.service.IChallengeService;
import com.itachallenge.challenge.service.JwtServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@WebFluxTest(controllers = FavoritesController.class)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class FavoritesControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PropertiesConfig config;

    @MockBean
    private JwtServiceImpl jwtService;

    @MockBean
    private IChallengeService challengeService;

    @Test
    void addChallengeToFavorites_Success_Returns200() {
        String challengeId = "existing_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        FavoriteDto expectedResponse = new FavoriteDto(true, 20);

        when(challengeService.addChallengeToFavorites(challengeId, userId)).thenReturn(Mono.just(expectedResponse));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.post()
                .uri("/itachallenge/api/v1/favorites/addChallenge/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FavoriteDto.class)
                .isEqualTo(expectedResponse);

        verify(challengeService, times(1)).addChallengeToFavorites(challengeId, userId);
    }

    @Test
    void addChallengeToFavorites_ChallengeNotFound_Returns404() {
        String challengeId = "nonExisting_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        String errorMessage = "ErrorMessage";

        when(challengeService.addChallengeToFavorites(challengeId, userId)).thenReturn(Mono.error(new ChallengeNotFoundReturn404Exception(errorMessage)));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.post()
                .uri("/itachallenge/api/v1/favorites/addChallenge/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(1)).addChallengeToFavorites(challengeId, userId);
    }

    @Test
    void addChallengeToFavorites_InternalServerError_Returns500() {
        String challengeId = "Existing_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        String errorMessage = "ErrorMessage";

        when(challengeService.addChallengeToFavorites(challengeId, userId)).thenReturn(Mono.error(new InternalServerErrorException(errorMessage)));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.post()
                .uri("/itachallenge/api/v1/favorites/addChallenge/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(1)).addChallengeToFavorites(challengeId, userId);
    }

    @Test
    void addChallengeToFavorites_InvalidHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String authHeader = "badHeader";
        String errorMessage = "ErrorMessage";

        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenThrow(new JwtException(errorMessage));

        webTestClient.post()
                .uri("/itachallenge/api/v1/favorites/addChallenge/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(0)).addChallengeToFavorites(anyString(), anyString());
    }

    @Test
    void addChallengeToFavorites_MissingHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String errorMessage = "ErrorMessage";

        when(jwtService.getUserUuIdFromAuthenticationHeader(null)).thenThrow(new JwtException(errorMessage));

        webTestClient.post()
                .uri("/itachallenge/api/v1/favorites/addChallenge/" + challengeId + "/favorites")
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(0)).addChallengeToFavorites(anyString(), anyString());
    }

    @Test
    void removeChallengeFromFavorite_Success_Returns200() {
        String challengeId = "existing_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        FavoriteDto expectedResponse = new FavoriteDto(false, 20);

        when(challengeService.removeChallengeFromFavorites(challengeId, userId)).thenReturn(Mono.just(expectedResponse));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.delete()
                .uri("/itachallenge/api/v1/favorites/removeChallenge/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isOk()
                .expectBody(FavoriteDto.class)
                .isEqualTo(expectedResponse);

        verify(challengeService, times(1)).removeChallengeFromFavorites(challengeId, userId);
    }

    @Test
    void removeChallengeFromFavorite_ChallengeNotFound_Returns404() {
        String challengeId = "nonExisting_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        String errorMessage = "ErrorMessage";

        when(challengeService.removeChallengeFromFavorites(challengeId, userId)).thenReturn(Mono.error(new ChallengeNotFoundReturn404Exception(errorMessage)));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.delete()
                .uri("/itachallenge/api/v1/favorites/removeChallenge/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(1)).removeChallengeFromFavorites(challengeId, userId);
    }

    @Test
    void removeChallengeFromFavorite_InternalServerError_Returns500() {
        String challengeId = "Existing_challengeId";
        String userId = "existing_userId";
        String authHeader = "validAuthHeader";

        String errorMessage = "ErrorMessage";

        when(challengeService.removeChallengeFromFavorites(challengeId, userId)).thenReturn(Mono.error(new InternalServerErrorException(errorMessage)));
        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenReturn(userId);

        webTestClient.delete()
                .uri("/itachallenge/api/v1/favorites/removeChallenge/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(1)).removeChallengeFromFavorites(challengeId, userId);
    }

    @Test
    void removeChallengeFromFavorite_InvalidHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String authHeader = "BadHeader";
        String errorMessage = "Error message";

        when(jwtService.getUserUuIdFromAuthenticationHeader(authHeader)).thenThrow(new JwtException(errorMessage));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/favorites/removeChallenge/" + challengeId + "/favorites")
                .header("Authorization", authHeader)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(0)).removeChallengeFromFavorites(anyString(), anyString());
    }

    @Test
    void removeChallengeFromFavorite_MissingHeader_Returns400() {
        String challengeId = "Existing_challengeId";
        String errorMessage = "ErrorMessage";

        when(jwtService.getUserUuIdFromAuthenticationHeader(null)).thenThrow(new JwtException(errorMessage));

        webTestClient.delete()
                .uri("/itachallenge/api/v1/favorites/removeChallenge/" + challengeId + "/favorites")
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.BAD_REQUEST)
                .expectBody(MessageDto.class)
                .value(messageDto -> Assertions.assertEquals(errorMessage, messageDto.getMessage()));

        verify(challengeService, times(0)).removeChallengeFromFavorites(anyString(), anyString());

    }
}
