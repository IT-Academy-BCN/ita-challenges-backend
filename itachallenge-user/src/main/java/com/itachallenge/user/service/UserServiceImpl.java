package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.repository.UserRepository;
import com.itachallenge.userinteraction.document.bookmark.BookmarkDocument;
import com.itachallenge.userinteraction.service.bookmark.BookmarkService;
import org.springframework.stereotype.Service;

import com.itachallenge.userinteraction.repository.bookmark.BookmarkRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BookmarkService bookmarkService;

    public UserServiceImpl(UserRepository userRepository, BookmarkService bookmarkService) {
        this.userRepository = userRepository;
        this.bookmarkService = bookmarkService;
    }

    @Override
    public Mono<UserDocument> getUser(String githubUsername) {
        return userRepository.findByUsername(githubUsername)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }

    /**
     * {@inheritDoc}
     * @deprecated Use {@link BookmarkService#addChallengeToBookmarks(String, String)} instead.
     */
    @Override
    @Deprecated(forRemoval = true)
    public Mono<Boolean> addChallengeToBookmarks(String userId, String challengeId) {
        return bookmarkService.addChallengeToBookmarks(userId, challengeId);
    }

    /**
     * {@inheritDoc}
     * @deprecated Use {@link BookmarkService#addChallengeToBookmarks(String, String)} instead.
     */
    @Override
    @Deprecated(forRemoval = true)
    public Mono<Boolean> deleteChallengeFromBookmarks(String userId, String challengeId) {
        return bookmarkService.deleteChallengeFromBookmarks(userId, challengeId);
    }

    @Override
    public Mono<UserDocument> getUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }
}

