package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.FavoriteDto;
import com.itachallenge.challenge.exception.BadUUIDException;
import com.itachallenge.challenge.exception.ChallengeNotFoundException;
import com.itachallenge.challenge.exception.InternalServerErrorException;
import com.itachallenge.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements IFavoriteService {

    private static final Logger log = LoggerFactory.getLogger(FavoriteServiceImpl.class);
    private static final Pattern UUID_FORM = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);
    private static final String CHALLENGE_NOT_FOUND_ERROR = "Challenge with id: %s not found";

    private final ChallengeRepository challengeRepository;
    private final IUserService userService;

    @Override
    public Mono<FavoriteDto> addChallengeToFavorites(String challengeId, String userId) {
        Mono<UUID> challengeIdMono = validateUUID(challengeId);
        Mono<UUID> userIdMono = validateUUID(userId);

        return Mono.zip(challengeIdMono, userIdMono)
                .flatMap(tuple -> {
                    UUID challengeUuid = tuple.getT1();
                    UUID userUuid = tuple.getT2();

                    return challengeRepository.findByUuid(challengeUuid)
                            .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeUuid))))
                            .flatMap(challenge -> userService.addChallengeToFavorites(userUuid.toString(), challengeUuid.toString())
                                    .onErrorResume(e -> Mono.error(new InternalServerErrorException(e.getMessage())))
                                    .flatMap(added -> {
                                        if (Boolean.TRUE.equals(added) ||
                                                Optional.ofNullable(challenge.getTimesFavorite()).orElse(0) == 0) {
                                            challenge.increaseTimesFavorite();
                                            return challengeRepository.save(challenge);
                                        }
                                        return Mono.just(challenge);
                                    })
                                    .map(saved -> new FavoriteDto(true, saved.getTimesFavorite())));
                });
    }

    @Override
    public Mono<FavoriteDto> removeChallengeFromFavorites(String challengeId, String userId) {
        Mono<UUID> challengeIdMono = validateUUID(challengeId);
        Mono<UUID> userIdMono = validateUUID(userId);

        return Mono.zip(challengeIdMono, userIdMono)
                .flatMap(tuple -> {
                    UUID challengeUuid = tuple.getT1();
                    UUID userUuid = tuple.getT2();

                    return challengeRepository.findByUuid(challengeUuid)
                            .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeUuid))))
                            .flatMap(challenge -> userService.removeChallengeFromFavorites(userUuid.toString(), challengeUuid.toString())
                                    .onErrorResume(e -> Mono.error(new InternalServerErrorException(e.getMessage())))
                                    .flatMap(removed -> {
                                        if (Boolean.TRUE.equals(removed) ||
                                                Optional.ofNullable(challenge.getTimesFavorite()).orElse(0) == 0) {
                                            challenge.decreaseTimesFavorite();
                                            return challengeRepository.save(challenge);
                                        }
                                        return Mono.just(challenge);
                                    })
                                    .map(saved -> new FavoriteDto(false, saved.getTimesFavorite())));
                });
    }

    private Mono<UUID> validateUUID(String id) {
        boolean validUUID = id != null && UUID_FORM.matcher(id).matches();

        if (!validUUID) {
            log.warn("Invalid ID format.");
            return Mono.error(new BadUUIDException("Invalid ID format. Please indicate the correct format."));
        }

        return Mono.just(UUID.fromString(id));
    }
}