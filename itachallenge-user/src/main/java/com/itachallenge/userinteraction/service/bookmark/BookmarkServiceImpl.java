package com.itachallenge.userinteraction.service.bookmark;

import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class BookmarkServiceImpl implements BookmarkService {

    private final UserRepository userRepository;

    public BookmarkServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Set<UUID>> getUserBookmarks(String userId) {
        return parseAndValidateUUID(userId)
                .flatMap(userUuid ->
                        userRepository.findById(userUuid)
                                .switchIfEmpty(Mono.error(new NotFoundException("User not found with id: " + userId)))
                                .map(user -> Optional.ofNullable(user.getBookmarkChallenges()).orElseGet(HashSet::new))
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
}
