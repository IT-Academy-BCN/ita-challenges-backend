package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserDocument> getUser(String githubUsername);

    /**
     * @deprecated This method is deprecated because the bookmark logic has been moved
     * to the specialized {@link com.itachallenge.userinteraction.service.bookmark.BookmarkService}.
     * Use the new bookmark endpoints instead.
     */
    @Deprecated(forRemoval = true)
    Mono<Boolean> addChallengeToBookmarks(String userId, String challengeId);

    /**
     * @deprecated This method is deprecated because the bookmark logic has been moved
     * to the specialized {@link com.itachallenge.userinteraction.service.bookmark.BookmarkService}.
     * Use the new bookmark endpoints instead.
     */
    @Deprecated(forRemoval = true)
    Mono<Boolean> deleteChallengeFromBookmarks(String userId, String challengeId);

    Mono<UserDocument> getUserById(String userId);
}
