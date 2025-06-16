package com.itachallenge.user.service;

import com.itachallenge.user.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.stereotype.Service;

import com.itachallenge.user.document.enums.ChallengeStatus;
import com.itachallenge.user.exception.BadRequestException;
import com.itachallenge.user.exception.InternalServerErrorException;

import reactor.core.publisher.Mono;

@Service
public class ChallengeServiceImpl implements IChallengeService {

    private static final Logger log = LoggerFactory.getLogger(ChallengeServiceImpl.class);

    private final WebClient.Builder webClientBuilder;

    private final String challengeServiceUrl;
    private static final String X_SOLVED_MESSAGE = "X-Solved-Message";

    public ChallengeServiceImpl(
            WebClient.Builder webClientBuilder, @Value("${challenge.service.url}") String challengeServiceUrl) {
        this.webClientBuilder = webClientBuilder;
        this.challengeServiceUrl = challengeServiceUrl;
    }

    @Override
    public Mono<Boolean> addChallengeToSolved(String challengeId) {
        return callEndpoint(challengeId, ChallengeStatus.ENDED, X_SOLVED_MESSAGE, HttpMethod.POST);
    }

    private Mono<Boolean> callEndpoint(String challengeId, ChallengeStatus type, String errorHeader, HttpMethod method) {
        String url = buildUrl(challengeId, type.toString().toLowerCase());
        log.debug("Calling {} endpoint with method={} and URL={}", type.name().toLowerCase(), method, url);

        return webClientBuilder.build()
                .method(method)
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    log.info("Challenge not found with id: {}", challengeId);
                    return Mono.error(new NotFoundException("Challenge not found"));
                })
                .onStatus(HttpStatus.BAD_REQUEST::equals, response -> {
                    String errorMessage = response.headers().header(errorHeader).stream()
                            .findFirst().orElse("Unknown error");
                    log.warn("ChallengeService returned 400: {}", errorMessage);
                    return Mono.error(new BadRequestException(errorMessage));
                })
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, response -> {
                    String errorMessage = response.headers().header(errorHeader).stream()
                            .findFirst().orElse("Unknown error");
                    log.warn("ChallengeService returned 500: {}", errorMessage);
                    return Mono.error(new InternalServerErrorException(errorMessage));
                })
                .bodyToMono(Boolean.class);
    }

    private String buildUrl(String challengeId, String type){
        return UriComponentsBuilder.fromHttpUrl(challengeServiceUrl)
                .path("/itachallenge/api/v1/challenge/solved/{type}/{challengeId}")
                .buildAndExpand(type, challengeId)
                .toUriString();
    }

}