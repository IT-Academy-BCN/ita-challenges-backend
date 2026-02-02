package com.itachallenge.user.controller.userinteraction.bookmark;

import com.itachallenge.userinteraction.service.bookmark.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.Set;
import java.util.UUID;

public class BookmarkLegacyController {
    private final BookmarkService bookmarkService;

    public BookmarkLegacyController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    /**
     * @deprecated This endpoint is deprecated because the domain logic has moved
     * to a subresource structure. Use {@link BookmarkController#getUserBookmarks(String)} instead.
     */
    @Operation(summary = "DEPRECATED: Use /itachallenge/api/v1/users/{userId}/bookmarks")
    @GetMapping("/{userId}/bookmarks")
    @Deprecated(since = "3.1.4-RELEASE", forRemoval = true)
    public Mono<ResponseEntity<Set<UUID>>> getUserBookmarksLegacy(@PathVariable String userId) {
        return bookmarkService.getUserBookmarks(userId)
                .map(bookmarks -> ResponseEntity.ok()
                        .header("Deprecation", "true")
                        .body(bookmarks));
    }
}
