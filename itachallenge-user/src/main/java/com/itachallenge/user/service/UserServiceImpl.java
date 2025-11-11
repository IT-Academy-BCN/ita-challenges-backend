package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.itachallenge.userinteraction.document.favorite.FavoriteDocument;
import com.itachallenge.userinteraction.repository.favorite.FavoriteRepository;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND = "User not found";
    private static final String USER_NOT_FOUND_WITH_ID = "User not found with id: ";

    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;

    public UserServiceImpl(UserRepository userRepository, FavoriteRepository favoriteRepository) {
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Mono<UserDocument> getUser(String githubUsername) {
        return userRepository.findByUsername(githubUsername)
                .switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND)));
    }

    //TODO : TO IMPLEMENT TO FAVORITE SERVICE IMPL
    @Override
    public Mono<Boolean> addChallengeToFavorites(String userId, String challengeId) {

        return Mono.zip(parseAndValidateUUID(userId), parseAndValidateUUID(challengeId))
                .flatMap(uuidTuple -> {
                    UUID userUuid = uuidTuple.getT1();
                    UUID challengeUuid = uuidTuple.getT2();

                    return userRepository.findById(userUuid)
                            .switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND)))
                            .flatMap(user -> addToFavorites(userUuid, challengeUuid));
                });
    }

    @Override
    public Mono<Boolean> addChallengeToBookmarks(String userId, String challengeId) {
        return Mono.zip(parseAndValidateUUID(userId), parseAndValidateUUID(challengeId))
                .flatMap(uuidTuple -> {
                    UUID userUuid = uuidTuple.getT1();
                    UUID challengeUuid = uuidTuple.getT2();

                    return userRepository.findById(userUuid)
                            .switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND)))
                            .flatMap(user -> addToBookmarks(user, challengeUuid));
                });
    }

    //TODO : TO IMPLEMENT IN FAVORITE SERVICE IMPL
    @Override
    public Mono<Boolean> deleteChallengeFromFavorites(String userId, String challengeId) {
        return Mono.zip(parseAndValidateUUID(userId), parseAndValidateUUID(challengeId))
                .flatMap(uuidTuple -> {
                    UUID userUuid = uuidTuple.getT1();
                    UUID challengeUuid = uuidTuple.getT2();

                    return userRepository.findById(userUuid)
                            .switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND)))
                            .flatMap(user -> deleteFromFavorites(userUuid, challengeUuid));
                });
    }

    @Override
    public Mono<Boolean> deleteChallengeFromBookmarks(String userId, String challengeId) {
        return Mono.zip(parseAndValidateUUID(userId), parseAndValidateUUID(challengeId))
                .flatMap(uuidTuple -> {
                    UUID userUuid = uuidTuple.getT1();
                    UUID challengeUuid = uuidTuple.getT2();

                    return userRepository.findById(userUuid)
                            .switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND)))
                            .flatMap(user -> deleteFromBookmarks(user, challengeUuid));
                });
    }


    //TODO : TO IMPLEMENT IN FAVORITE SERVICE IMPL
    private Mono<Boolean> addToFavorites(UUID userUuid, UUID challengeUuid) {
        return favoriteRepository.existsByUserIdAndChallengeId(userUuid, challengeUuid)
                .flatMap(exists -> {
                    if (exists.booleanValue())
                        return Mono.just(false);

                    FavoriteDocument favorite = new FavoriteDocument();
                    favorite.setUuid(UUID.randomUUID());
                    favorite.setUserId(userUuid);
                    favorite.setChallengeId(challengeUuid);

                    return favoriteRepository.save(favorite)
                            .thenReturn(true);
                });
    }

    private Mono<Boolean> addToBookmarks(UserDocument user, UUID challengeUuid) {
        Set<UUID> bookmarks = Optional.ofNullable(user.getBookmarkChallenges())
                .orElseGet(HashSet::new);

        boolean added = bookmarks.add(challengeUuid);

        if (added) {
            user.setBookmarkChallenges(bookmarks);
            return userRepository.save(user).then(Mono.just(true));
        }

        return Mono.just(false);
    }

    //TODO : TO IMPLEMENT IN FAVORITE SERVICE IMPL
    private Mono<Boolean> deleteFromFavorites(UUID userId, UUID challengeUuid) {
        return favoriteRepository.findByUserIdAndChallengeId(userId, challengeUuid)
                .flatMap(favorite ->
                        favoriteRepository.delete(favorite)
                                .then(Mono.just(true))
                )
                .switchIfEmpty(Mono.just(false));
    }

    private Mono<Boolean> deleteFromBookmarks(UserDocument user, UUID challengeUuid) {
        Set<UUID> bookmarks = Optional.ofNullable(user.getBookmarkChallenges())
                .orElseGet(HashSet::new);

        boolean deleted = bookmarks.remove(challengeUuid);

        if (deleted) {
            user.setBookmarkChallenges(bookmarks);
            return userRepository.save(user).then(Mono.just(true));
        }

        return Mono.just(false);
    }

    @Override
    public Mono<Set<UUID>> getUserFavorites(String userId) {
        return parseAndValidateUUID(userId)
                .flatMap(userUuid ->
                        userRepository.findById(userUuid)
                                .switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND + userId)))
                                .map(user -> Optional.ofNullable(user.getFavoriteChallenges()).orElseGet(HashSet::new))
                );
    }
    private Mono<UUID> parseAndValidateUUID(String id) {

        if (id == null || id.isEmpty()) {
            return Mono.error(new BadUUIDException("Invalid ID format"));
        }

        try {
            return Mono.just(UUID.fromString(id));
        } catch (IllegalArgumentException ex) {
            return Mono.error(new BadUUIDException("Invalid ID format"));
        }
    }

    @Override
    public Mono<Set<UUID>> getUserBookmarks(String userId) {
        return parseAndValidateUUID(userId)
                .flatMap(userUuid ->
                        userRepository.findById(userUuid)
                                .switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND_WITH_ID + userId)))
                                .map(user -> Optional.ofNullable(user.getBookmarkChallenges()).orElseGet(HashSet::new))
                );
    }
    
    @Override
    public Mono<UserDocument> getUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .switchIfEmpty(Mono.error(new NotFoundException(USER_NOT_FOUND)));
    }
    
}
