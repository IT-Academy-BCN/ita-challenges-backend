package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.repository.UserRepository;
import com.itachallenge.userinteraction.document.bookmark.BookmarkDocument;
import com.itachallenge.userinteraction.service.bookmark.BookmarkServiceImpl;
import org.springframework.stereotype.Service;

import com.itachallenge.userinteraction.repository.bookmark.BookmarkRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BookmarkServiceImpl bookmarkServiceImpl;

    public UserServiceImpl(UserRepository userRepository, BookmarkServiceImpl bookmarkServiceImpl) {
        this.userRepository = userRepository;
        this.bookmarkServiceImpl = bookmarkServiceImpl;
    }

    @Override
    public Mono<UserDocument> getUser(String githubUsername) {
        return userRepository.findByUsername(githubUsername)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }

    @Override
    public Mono<UserDocument> getUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }

    /**
     * @deprecated This method is scheduled for removal.
     */
    @Deprecated(forRemoval = true)
    @Override
    public Mono<Boolean> addChallengeToBookmarks(String userId, String challengeId) {
        return bookmarkServiceImpl.addChallengeToBookmarks(userId, challengeId);
    }

    /**
     * @deprecated This method is scheduled for removal.
     */
    @Deprecated(forRemoval = true)
    @Override
    public Mono<Boolean> deleteChallengeFromBookmarks(String userId, String challengeId) {
        return bookmarkServiceImpl.deleteChallengeFromBookmarks(userId, challengeId);
    }
}