package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<UserDocument> getUser(String githubUsername) {
        return userRepository.findByUsername(githubUsername);
    }

    public Mono<Boolean> addChallengeToFavorites(UUID userId, UUID challengeId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")))
                .flatMap(user -> {
                    Set<UUID> favorites = Optional.ofNullable(user.getFavoriteChallenges())
                            .orElseGet(HashSet::new);

                    boolean added = favorites.add(challengeId);

                    if (added) {
                        user.setFavoriteChallenges(favorites);
                        return userRepository.save(user).then(Mono.just(true));
                    }

                    return Mono.just(false);
                });
    }

}
