package com.itachallenge.userinteraction.service.bookmark;
import com.itachallenge.common.exception.BadUUIDException;
import com.itachallenge.common.exception.NotFoundException;
import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.repository.UserRepository;
import com.itachallenge.userinteraction.document.bookmark.BookmarkDocument;
import com.itachallenge.userinteraction.repository.bookmark.BookmarkRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
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

    private static final String USER_NOT_FOUND_WITH_ID = "User not found with id: ";

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        bookmarkService = new BookmarkServiceImpl(userRepository, bookmarkRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) mocks.close();
    }

    @Test
    @DisplayName("getUserBookmarks returns an empty set when the user exists but has no bookmarks")
    void getUserBookmarks_WhenUserHasNoBookmarks_ReturnsEmptySet() {
        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(Mono.just(true));
        when(bookmarkRepository.findByUserId(userId)).thenReturn(Flux.empty());

        StepVerifier.create(bookmarkService.getUserBookmarks(userId.toString()))
                .expectNextMatches(Set::isEmpty)
                .verifyComplete();

        verify(userRepository, times(1)).existsById(userId);
        verify(bookmarkRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("getUserBookmarks returns bookmarks when the user exists")
    void getUserBookmarks_WhenUserExistsWithFavorites_ReturnsBookmarks() {
        UUID userId = UUID.randomUUID();
        UUID challengeId1 = UUID.randomUUID();
        UUID challengeId2 = UUID.randomUUID();

        BookmarkDocument fav1 = BookmarkDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId1)
                .build();
        BookmarkDocument fav2 = BookmarkDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId2)
                .build();

        when(userRepository.existsById(userId)).thenReturn(Mono.just(true));
        when(bookmarkRepository.findByUserId(userId)).thenReturn(Flux.just(fav1, fav2));

        StepVerifier.create(bookmarkService.getUserBookmarks(userId.toString()))
                .expectNextMatches(bookmarks -> bookmarks.contains(challengeId1) && bookmarks.contains(challengeId2))
                .verifyComplete();

        verify(userRepository, times(1)).existsById(userId);
        verify(bookmarkRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("getUserBookmarks throws NotFoundException when the user does not exist")
    void getUserBookmarks_WhenUserNotFound_ThrowsNotFoundException() {
        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(Mono.just(false));

        StepVerifier.create(bookmarkService.getUserBookmarks(userId.toString()))
                .expectErrorMatches(error ->
                        error instanceof NotFoundException &&
                                error.getMessage().equals(USER_NOT_FOUND_WITH_ID + userId))
                .verify();

        verify(userRepository, times(1)).existsById(userId);
        verify(bookmarkRepository, times(0)).findByUserId(userId);
    }

    @Test
    @DisplayName("getUserBookmarks throws BadUUIDException when the UUID format is invalid")
    void getUserBookmarks_WhenInvalidUUID_ThrowsBadUUIDException() {
        String invalidUUID = "invalid-uuid";

        StepVerifier.create(bookmarkService.getUserBookmarks(invalidUUID))
                .expectErrorMatches(error ->
                        error instanceof BadUUIDException &&
                                error.getMessage().equals("Invalid ID format"))
                .verify();

        verify(userRepository, times(0)).existsById((UUID) any());
        verify(bookmarkRepository, times(0)).findByUserId(any());
    }

    @Test
    @DisplayName("getUserBookmarks propagates repository errors correctly")
    void getUserBookmarks_WhenRepositoryError_PropagatesError() {
        UUID userId = UUID.randomUUID();

        when(userRepository.existsById(userId)).thenReturn(Mono.just(true));
        when(bookmarkRepository.findByUserId(userId)).thenReturn(Flux.error(new RuntimeException("DB error")));

        StepVerifier.create(bookmarkService.getUserBookmarks(userId.toString()))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("DB error"))
                .verify();

        verify(userRepository, times(1)).existsById(userId);
        verify(bookmarkRepository, times(1)).findByUserId(userId);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideAddBookmarkSuccessCases")
    void addChallengeToBookmarks_ShouldReturnTrue_WhenBookmarkDoesNotExist(String testCase) {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(bookmarkRepository.existsByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(false));
        when(bookmarkRepository.save(any(BookmarkDocument.class)))
                .thenReturn(Mono.just(new BookmarkDocument()));

        StepVerifier.create(bookmarkService.addChallengeToBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(bookmarkRepository, times(1)).existsByUserIdAndChallengeId(userId, challengeId);
        verify(bookmarkRepository, times(1)).save(any(BookmarkDocument.class));
    }

    private static Stream<Arguments> provideAddBookmarkSuccessCases() {
        return Stream.of(
                Arguments.of("When bookmarks is null"),
                Arguments.of("When bookmarks is empty"),
                Arguments.of("When bookmarks has values")
        );
    }

    @Test
    void addChallengeToBookmarks_ShouldReturnFalse_WhenBookmarksAlreadyContainsChallenge() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(bookmarkRepository.existsByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(true));

        StepVerifier.create(bookmarkService.addChallengeToBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(bookmarkRepository, times(1)).existsByUserIdAndChallengeId(userId, challengeId);
        verify(bookmarkRepository, never()).save(any());
    }

    @Test
    void addChallengeToBookmarks_ShouldThrowBadRequestException_WhenChallengeUuidIsNull() {
        StepVerifier.create(bookmarkService.addChallengeToBookmarks(UUID.randomUUID().toString(), null))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void addChallengeToBookmarks_ShouldThrowBadRequestException_WhenUserUuidIsNotValid() {
        StepVerifier.create(bookmarkService.addChallengeToBookmarks("invalidUuid", UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void addChallengeToBookmarks_ShouldThrowBadRequestException_WhenChallengeUuidIsNotValid() {
        StepVerifier.create(bookmarkService.addChallengeToBookmarks(UUID.randomUUID().toString(), "invalidUuid"))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("provideDeleteBookmarkFailureCases")
    void deleteChallengeFromBookmarks_ShouldReturnFalse_WhenBookmarkNotFound(String testCase) {

        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(bookmarkRepository.findByUserIdAndChallengeId(any(UUID.class), any(UUID.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(bookmarkService.deleteChallengeFromBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(bookmarkRepository).findByUserIdAndChallengeId(any(UUID.class), any(UUID.class));
        verify(bookmarkRepository, never()).delete(any());
    }

    private static Stream<Arguments> provideDeleteBookmarkFailureCases() {
        return Stream.of(
                Arguments.of("When bookmarks is null"),
                Arguments.of("When bookmarks is empty"),
                Arguments.of("When challenge not in bookmarks")
        );
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldReturnTrue_WhenBookmarksAlreadyContainsChallenge() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, 0);
        BookmarkDocument bookmark = BookmarkDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(bookmarkRepository.findByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(bookmark));
        when(bookmarkRepository.delete(any(BookmarkDocument.class))).thenReturn(Mono.empty());

        StepVerifier.create(bookmarkService.deleteChallengeFromBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        verify(userRepository).findById(userId);
        verify(bookmarkRepository).findByUserIdAndChallengeId(userId, challengeId);
        verify(bookmarkRepository).delete(bookmark);
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldThrowNotFoundException_WhenUserNotFound() {

        when(userRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(bookmarkService.deleteChallengeFromBookmarks(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(NotFoundException.class, throwable);
                    assertEquals("User not found", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldThrowBadRequestException_WhenUserUuidIsNull() {
        StepVerifier.create(bookmarkService.deleteChallengeFromBookmarks(null, UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldThrowBadRequestException_WhenChallengeUuidIsNull() {
        StepVerifier.create(bookmarkService.deleteChallengeFromBookmarks(UUID.randomUUID().toString(), null))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldThrowBadRequestException_WhenUserUuidIsNotValid() {
        StepVerifier.create(bookmarkService.deleteChallengeFromBookmarks("invalidUuid", UUID.randomUUID().toString()))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldThrowBadRequestException_WhenChallengeUuidIsNotValid() {
        StepVerifier.create(bookmarkService.deleteChallengeFromBookmarks(UUID.randomUUID().toString(), "invalidUuid"))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }
}
