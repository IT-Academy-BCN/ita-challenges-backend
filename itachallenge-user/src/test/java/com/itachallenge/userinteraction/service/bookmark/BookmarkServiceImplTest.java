package com.itachallenge.userinteraction.service.bookmark;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookmarkServiceImpl bookmarkService;

    private UUID userId;
    private UUID challengeId1;
    private UUID challengeId2;
    private UserDocument userWithBookmarks;
    private UserDocument userWithoutBookmarks;
    
    @BeforeEach
    void setUp() {
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

    @Test
    @DisplayName("Get user bookmarks - Success - Returns set of bookmarked challenges")
    void getUserBookmarks_WhenUserExistsWithBookmarks_ReturnsSet() {
        when(userRepository.findById(userId)).thenReturn(Mono.just(userWithBookmarks));

        bookmarkService.getUserBookmarks(userId.toString())
                .as(StepVerifier::create)
                .assertNext(bookmarks -> {
                    assertEquals(2, bookmarks.size());
                    assertTrue(bookmarks.contains(challengeId1));
                    assertTrue(bookmarks.contains(challengeId2));
                })
                .verifyComplete();
                
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Get user bookmarks - Success - Returns empty set when user has no bookmarks")
    void getUserBookmarks_WhenUserHasNoBookmarks_ReturnsEmptySet() {
        when(userRepository.findById(userId)).thenReturn(Mono.just(userWithoutBookmarks));

        bookmarkService.getUserBookmarks(userId.toString())
                .as(StepVerifier::create)
                .assertNext(bookmarks -> assertTrue(bookmarks.isEmpty()))
                .verifyComplete();
                
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Get user bookmarks - Error - User not found")
    void getUserBookmarks_WhenUserNotFound_ReturnsError() {
        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        bookmarkService.getUserBookmarks(userId.toString())
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof NotFoundException);
                    assertEquals("User not found with id: " + userId, error.getMessage());
                })
                .verify();
                
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Get user bookmarks - Error - Invalid UUID format")
    void getUserBookmarks_WhenInvalidUUID_ReturnsBadUUIDException() {
        String invalidUUID = "invalid-uuid";

        bookmarkService.getUserBookmarks(invalidUUID)
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof BadUUIDException);
                    assertEquals("Invalid ID format", error.getMessage());
                })
                .verify();

        verify(userRepository, never()).findById(any(UUID.class));
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
    @DisplayName("Parse and validate UUID - Error - Invalid UUID format")
    void parseAndValidateUUID_WithInvalidFormat_ThrowsBadUUIDException() {
        assertThrows(BadUUIDException.class,
                () -> callGetUserBookmarks("not-a-uuid"));
    }
    
    @Test
    @DisplayName("Get user bookmarks - Success - When bookmarkChallenges is null, returns empty set")
    void getUserBookmarks_WhenBookmarkChallengesIsNull_ReturnsEmptySet() {
        UserDocument userWithNullBookmarks = new UserDocument();
        userWithNullBookmarks.setUuid(userId);
        userWithNullBookmarks.setBookmarkChallenges(null);
        
        when(userRepository.findById(userId)).thenReturn(Mono.just(userWithNullBookmarks));

        bookmarkService.getUserBookmarks(userId.toString())
                .as(StepVerifier::create)
                .assertNext(bookmarks -> assertTrue(bookmarks.isEmpty()))
                .verifyComplete();
                
        verify(userRepository, times(1)).findById(userId);
    }
    
    @Test
    @DisplayName("Get user bookmarks - Error - Repository throws exception")
    void getUserBookmarks_WhenRepositoryThrowsException_PropagatesException() {
        String errorMessage = "Database connection failed";
        when(userRepository.findById(userId)).thenReturn(Mono.error(new RuntimeException(errorMessage)));

        bookmarkService.getUserBookmarks(userId.toString())
                .as(StepVerifier::create)
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof RuntimeException);
                    assertEquals(errorMessage, error.getMessage());
                })
                .verify();
                
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Parse and validate UUID - Error - Very long string")
    void parseAndValidateUUID_WithVeryLongString_ThrowsBadUUIDException() {
        String longString = new String(new char[1000]).replace('\0', 'a');

        assertThrows(BadUUIDException.class,
                () -> callGetUserBookmarks(longString));
    }
    
    private void callGetUserBookmarks(String input) {
        bookmarkService.getUserBookmarks(input).block();
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
    
    @Test
    @DisplayName("Get user bookmarks - Success - Returns empty set when user document has no bookmarks")
    void getUserBookmarks_WhenUserDocumentHasNoBookmarks_ReturnsEmptySet() {
        UserDocument emptyUser = new UserDocument();
        emptyUser.setUuid(userId);
        when(userRepository.findById(userId)).thenReturn(Mono.just(emptyUser));

        bookmarkService.getUserBookmarks(userId.toString())
                .as(StepVerifier::create)
                .assertNext(bookmarks -> assertTrue(bookmarks.isEmpty()))
                .verifyComplete();
                
        verify(userRepository, times(1)).findById(userId);
    }
}
