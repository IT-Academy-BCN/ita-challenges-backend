package com.itachallenge.userinteraction.service.bookmark;

import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.common.exception.NotFoundException;
import com.itachallenge.user.repository.UserRepository;
import com.itachallenge.userinteraction.document.bookmark.BookmarkDocument;
import com.itachallenge.userinteraction.repository.bookmark.BookmarkRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookmarkServiceImpl implements BookmarkService {

    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;

    private static final String USER_NOT_FOUND_WITH_ID = "User not found with id: ";

    public BookmarkServiceImpl(UserRepository userRepository, BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Set<UUID>> getUserBookmarks(String userId) {
        return parseAndValidateUUID(userId)
                .flatMap(userUuid ->
                        userRepository.existsById(userUuid)
                                .flatMap(exists -> exists == Boolean.TRUE
                                        ? bookmarkRepository.findByUserId(userUuid)
                                        .map(BookmarkDocument::getChallengeId)
                                        .collect(Collectors.toSet())
                                        : Mono.error(new NotFoundException(USER_NOT_FOUND_WITH_ID + userId))
                                )
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
