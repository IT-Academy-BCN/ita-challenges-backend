package com.itachallenge.userinteraction.service.bookmark;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.repository.UserRepository;
import com.itachallenge.userinteraction.document.bookmark.BookmarkDocument;
import com.itachallenge.userinteraction.repository.bookmark.BookmarkRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceImplTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private BookmarkRepository bookmarkRepository;

    @InjectMocks
    private BookmarkServiceImpl bookmarkService;

    private AutoCloseable mocks;

    private UUID userId;
    private UUID challengeId1;
    private UUID challengeId2;
    private UserDocument userWithBookmarks;
    private UserDocument userWithoutBookmarks;
    
    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        challengeId1 = UUID.randomUUID();
        challengeId2 = UUID.randomUUID();

        userWithBookmarks = new UserDocument();
        userWithBookmarks.setUuid(userId);
        userWithBookmarks.setBookmarkChallenges(Set.of(challengeId1, challengeId2));
        
        userWithoutBookmarks = new UserDocument();
        userWithoutBookmarks.setUuid(userId);
        userWithoutBookmarks.setBookmarkChallenges(Collections.emptySet());
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) mocks.close();
    }
    
    @Test
    @DisplayName("Get user bookmarks - Error - Null user ID")
    void getUserBookmarks_WhenNullUserId_ReturnsBadUUIDException() {
        bookmarkService.getUserBookmarks(null)
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BadUUIDException);
                    assertEquals("Invalid ID format", error.getMessage());
                })
                .verify();

        verify(userRepository, never()).findById(any(UUID.class));
    }
    
    @Test
    @DisplayName("Get user bookmarks - Error - Empty user ID")
    void getUserBookmarks_WhenEmptyUserId_ReturnsBadUUIDException() {
        bookmarkService.getUserBookmarks("")
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BadUUIDException);
                    assertEquals("Invalid ID format", error.getMessage());
                })
                .verify();

        verify(userRepository, never()).findById(any(UUID.class));
    }

    @Test
    @DisplayName("getUserBookmarks - Success - Returns user's challenge bookmarks")
    void getUserBookmarks_UserExists_ReturnsBookmarks() {

        UUID userId = UUID.randomUUID();
        UUID challenge1 = UUID.randomUUID();
        UUID challenge2 = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(Mono.just(true));
        when(bookmarkRepository.findByUserId(userId))
                .thenReturn(Flux.just(
                        BookmarkDocument.builder()
                                .userId(userId)
                                .challengeId(challenge1)
                                .build(),
                        BookmarkDocument.builder()
                                .userId(userId)
                                .challengeId(challenge2)
                                .build()
                ));

        bookmarkService.getUserBookmarks(userId.toString())
                .as(StepVerifier::create)
                .assertNext(result -> {
                    assertEquals(2, result.size());
                    assertTrue(result.contains(challenge1));
                    assertTrue(result.contains(challenge2));
                })
                .verifyComplete();

        verify(userRepository).existsById(userId);
        verify(bookmarkRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Get user bookmarks - Success - User with bookmarks")
    void getUserBookmarks_WhenUserExistsWithBookmarks_ReturnsBookmarks() {
        String userIdStr = userId.toString();
        Set<UUID> expectedBookmarks = Set.of(challengeId1, challengeId2);
        BookmarkDocument bookmark1 = BookmarkDocument.builder()
                .userId(userId)
                .challengeId(challengeId1)
                .build();
        BookmarkDocument bookmark2 = BookmarkDocument.builder()
                .userId(userId)
                .challengeId(challengeId2)
                .build();

        when(userRepository.existsById(userId)).thenReturn(Mono.just(true));
        when(bookmarkRepository.findByUserId(userId))
                .thenReturn(Flux.just(bookmark1, bookmark2));

        Mono<Set<UUID>> result = bookmarkService.getUserBookmarks(userIdStr);

        StepVerifier.create(result)
                .expectNextMatches(bookmarks -> {
                    assertEquals(expectedBookmarks.size(), bookmarks.size());
                    assertTrue(bookmarks.containsAll(expectedBookmarks));
                    return true;
                })
                .verifyComplete();

        verify(userRepository).existsById(userId);
        verify(bookmarkRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Get user bookmarks - Success - User without bookmarks")
    void getUserBookmarks_WhenUserExistsWithoutBookmarks_ReturnsEmptySet() {
        String userIdStr = userId.toString();

        when(userRepository.existsById(userId)).thenReturn(Mono.just(true));
        when(bookmarkRepository.findByUserId(userId)).thenReturn(Flux.empty());

        Mono<Set<UUID>> result = bookmarkService.getUserBookmarks(userIdStr);

        StepVerifier.create(result)
                .expectNextMatches(Set::isEmpty)
                .verifyComplete();

        verify(userRepository).existsById(userId);
        verify(bookmarkRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Get user bookmarks - Error - User not found")
    void getUserBookmarks_WhenUserNotExists_ReturnsNotFoundException() {
        String userIdStr = userId.toString();
        String expectedMessage = "User not found with id: " + userIdStr;

        when(userRepository.existsById(userId)).thenReturn(Mono.just(false));

        Mono<Set<UUID>> result = bookmarkService.getUserBookmarks(userIdStr);

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof NotFoundException);
                    assertEquals(expectedMessage, error.getMessage());
                })
                .verify();

        verify(userRepository).existsById(userId);
        verify(bookmarkRepository, never()).findByUserId(any(UUID.class));
    }

    @Test
    @DisplayName("Parse and validate UUID - Success - Valid UUID")
    void parseAndValidateUUID_WithValidUUID_ReturnsUUID() {
        String validUUID = "123e4567-e89b-12d3-a456-426614174000";
        UUID expectedUUID = UUID.fromString(validUUID);

        Mono<UUID> result = bookmarkService.getUserBookmarks(validUUID)
                .then(Mono.just(expectedUUID));

        StepVerifier.create(result)
                .expectNext(expectedUUID)
                .verifyComplete();
    }

    @Test
    @DisplayName("Parse and validate UUID - Error - Null input")
    void parseAndValidateUUID_WithNullInput_ThrowsBadUUIDException() {
        StepVerifier.create(bookmarkService.getUserBookmarks(null))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BadUUIDException);
                    assertEquals("Invalid ID format", error.getMessage());
                })
                .verify();
    }

    @Test
    @DisplayName("Parse and validate UUID - Error - Empty input")
    void parseAndValidateUUID_WithEmptyInput_ThrowsBadUUIDException() {
        StepVerifier.create(bookmarkService.getUserBookmarks(""))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BadUUIDException);
                    assertEquals("Invalid ID format", error.getMessage());
                })
                .verify();
    }

    @Test
    @DisplayName("Parse and validate UUID - Error - Very long string")
    void parseAndValidateUUID_WithVeryLongString_ThrowsBadUUIDException() {
        String longString = new String(new char[1000]).replace('\0', 'a');

        StepVerifier.create(bookmarkService.getUserBookmarks(longString))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BadUUIDException);
                    assertTrue(error.getMessage().contains("Invalid ID format"));
                })
                .verify();
    }

    private Object callGetUserBookmarks(String uuid) {
        try {
            return bookmarkService.getUserBookmarks(uuid).block();
        } catch (Exception e) {
            if (e.getCause() instanceof BadUUIDException) {
                throw (BadUUIDException) e.getCause();
            } else if (e.getCause() != null) {
                throw new RuntimeException("Unexpected exception: " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage(), e);
            } else {
                throw new RuntimeException("Unexpected exception: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
            }
        }
    }

    @Test
    @DisplayName("Parse and validate UUID - Error - String with whitespace")
    void parseAndValidateUUID_WithWhitespace_ThrowsBadUUIDException() {
        String stringWithWhitespace = " 123e4567-e89b-12d3-a456-426614174000 ";
        
        assertThrows(BadUUIDException.class,
                () -> callGetUserBookmarksWithWhitespace(stringWithWhitespace));
    }
    
    private void callGetUserBookmarksWithWhitespace(String input) {
        bookmarkService.getUserBookmarks(input).block();
    }
}
