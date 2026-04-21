package com.itachallenge.user.controller.userinteraction.bookmark;

import com.itachallenge.userinteraction.service.bookmark.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;

@RestController
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

    @GetMapping("/itachallenge/api/v1/users/{userId}/bookmarks")
    public Mono<ResponseEntity<Set<UUID>>> getUserBookmarks(@PathVariable String userId) {
        return bookmarkService.getUserBookmarks(userId)
                .map(bookmarks -> {
                    log.info("Retrieved {} bookmark challenges for user {}", bookmarks.size(), userId);
                    return ResponseEntity.ok(bookmarks);
                });
    }

    @Operation(
            summary = "Add Challenge to User Bookmark Challenges",
            description = "Adds challenge to user Bookmarks",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "User ID",
                            required = true,
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "challengeId",
                            description = "Challenge ID",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Challenge is already in bookmarks",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "201",
                            description = "Challenge added to bookmarks",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request. The provided IDs have a bad format",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found. No user is found with the provided user id.",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred.",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @PostMapping("/itachallenge/api/v1/user/users/{userId}/bookmarks/{challengeId}")
    public Mono<ResponseEntity<Boolean>> addChallengeToBookmarks(@PathVariable String userId, @PathVariable String challengeId) {
        return bookmarkService.addChallengeToBookmarks(userId, challengeId)
                .map(added -> {
                    if (Boolean.TRUE.equals(added)) {
                        log.info("Challenge '{}' added to user '{}' bookmarks", challengeId, userId);
                        return ResponseEntity.status(HttpStatus.CREATED).body(true);
                    } else {
                        log.info("User's '{}' bookmarks already contain Challenge '{}'", userId, challengeId);
                        return ResponseEntity.ok().body(false);
                    }
                });
    }

    @Operation(
            summary = "Delete Challenge from User Bookmark Challenges",
            description = "Deletes challenge from user bookmarks",
            parameters = {
                    @Parameter(
                            name = "userId",
                            description = "User ID",
                            required = true,
                            in = ParameterIn.PATH
                    ),
                    @Parameter(
                            name = "challengeId",
                            description = "Challenge ID",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Challenge deleted from bookmarks or was not in bookmarks",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request. The provided IDs have a bad format",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found. No user is found with the provided user id.",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error. An unexpected error occurred.",
                            content = @Content(mediaType = "application/json")
                    )
            }
    )
    @DeleteMapping("/itachallenge/api/v1/user/users/{userId}/bookmarks/{challengeId}")
    public Mono<ResponseEntity<Boolean>> deleteChallengeFromBookmarks(@PathVariable String userId, @PathVariable String challengeId) {
        return bookmarkService.deleteChallengeFromBookmarks(userId, challengeId)
                .map(deleted -> {
                    if (Boolean.TRUE.equals(deleted)) {
                        log.info("Challenge '{}' deleted from user '{}' bookmarks", challengeId, userId);
                        return ResponseEntity.ok().body(true);
                    } else {
                        log.info("No change, User's '{}' bookmarks doesn't contain Challenge '{}'", userId, challengeId);
                        return ResponseEntity.ok().body(false);
                    }
                });
    }
}