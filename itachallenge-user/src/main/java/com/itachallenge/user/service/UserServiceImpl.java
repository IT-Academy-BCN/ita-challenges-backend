package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private static final Pattern UUID_FORM = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDocument> getUser(String githubUsername) {
        return userRepository.findByUsername(githubUsername);
    }

    @Override
    public Mono<Boolean> addChallengeToFavorites(String userId, String challengeId) {

        return Mono.zip(validateUUID(userId), validateUUID(challengeId))
                .flatMap(objects -> {
                    UUID userUuid = objects.getT1();
                    UUID challengeUuid = objects.getT2();

                    return userRepository.findById(userUuid)
                            .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                            .flatMap(user -> addToFavorites(user, challengeUuid));
                });
    }

    @Override
    public Mono<Boolean> deleteChallengeFromFavorites(String userId, String challengeId) {
        return Mono.zip(validateUUID(userId), validateUUID(challengeId))
                .flatMap(objects -> {
                    UUID userUuid = objects.getT1();
                    UUID challengeUuid = objects.getT2();

                    return userRepository.findById(userUuid)
                            .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                            .flatMap(user -> deleteFromFavorites(user, challengeUuid));
                });
    }

    private Mono<Boolean> addToFavorites(UserDocument user, UUID challengeUuid) {
        Set<UUID> favorites = Optional.ofNullable(user.getFavoriteChallenges())
                .orElseGet(HashSet::new);

        boolean added = favorites.add(challengeUuid);

        if (added) {
            user.setFavoriteChallenges(favorites);
            return userRepository.save(user).then(Mono.just(true));
        }

        return Mono.just(false);
    }

    private Mono<Boolean> deleteFromFavorites(UserDocument user, UUID challengeUuid) {
        Set<UUID> favorites = Optional.ofNullable(user.getFavoriteChallenges())
                .orElseGet(HashSet::new);

        boolean deleted = favorites.remove(challengeUuid);

        if (deleted) {
            user.setFavoriteChallenges(favorites);
            return userRepository.save(user).then(Mono.just(true));
        }

        return Mono.just(false);
    }

    private Mono<UUID> validateUUID(String id) {
        boolean validUUID = id != null && !id.isEmpty() && UUID_FORM.matcher(id).matches();

        if (!validUUID) {
            return Mono.error(new BadUUIDException("Invalid ID format"));
        }

        return Mono.just(UUID.fromString(id));
    }

}
