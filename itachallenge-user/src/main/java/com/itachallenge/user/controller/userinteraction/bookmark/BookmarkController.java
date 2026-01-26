package com.itachallenge.user.controller.userinteraction.bookmark;

import com.itachallenge.userinteraction.service.bookmark.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/itachallenge/api/v1")
public class BookmarkController {

    private static final Logger log = LoggerFactory.getLogger(BookmarkController.class);

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @Operation(
            summary = "Gets challenges marked as bookmarks by a user",
            description = "Returns all challenges that the specified user has marked as bookmark.",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "UUID of the user",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Set of bookmark challengeIds by user"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "400", description = "The provided IDs are not valid."),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
            }
    )

    @GetMapping("/userinteraction/bookmarks/{userId}")
    public Mono<ResponseEntity<Set<UUID>>> getUserBookmarks(@PathVariable String userId) {
        return bookmarkService.getUserBookmarks(userId)
                .map(bookmarks -> {
                    log.info("Retrieved {} bookmark challenges for user {}", bookmarks.size(), userId);
                    return ResponseEntity.ok(bookmarks);
                });
    }
    /**
     * @deprecated This endpoint is deprecated because the domain logic has moved
     * to userinteraction. Use {@link #getUserBookmarks(String)} instead.
     */
    @Operation(summary = "DEPRECATED: Use /userinteraction/bookmarks/{userId}")
    @GetMapping("/user/users/{userId}/bookmarks")
    @Deprecated(since = "3.1.4-RELEASE", forRemoval = true)
    public Mono<ResponseEntity<Set<UUID>>> getUserBookmarksLegacy(@PathVariable String userId) {
        return getUserBookmarks(userId)
                .map(response -> ResponseEntity.status(response.getStatusCode())
                        .header("Deprecation", "true")
                        .body(response.getBody()));
    }
}