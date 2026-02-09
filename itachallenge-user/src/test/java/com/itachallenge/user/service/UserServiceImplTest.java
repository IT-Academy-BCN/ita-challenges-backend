package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.common.exception.BadUUIDException;
import com.itachallenge.common.exception.NotFoundException;
import com.itachallenge.user.repository.UserRepository;
import com.itachallenge.userinteraction.document.bookmark.BookmarkDocument;
import com.itachallenge.userinteraction.repository.bookmark.BookmarkRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;


class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (mocks != null) {
            mocks.close();
        }
    }

    @Test
    void getUser_ShouldReturnUser_WhenUserExists() {
        String username = "existingUser";
        UserDocument existingUser = new UserDocument(UUID.randomUUID(), username, Role.ADMIN, 0);
        when(userRepository.findByUsername(username)).thenReturn(Mono.just(existingUser));

        StepVerifier.create(userService.getUser(username))
                .expectNext(existingUser)
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void getUser_ShouldReturnNotFoundException_WhenUserDoesNotExist() {
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Mono.empty());

        StepVerifier.create(userService.getUser(username))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(NotFoundException.class, error);
                    assertEquals("User not found", error.getMessage());
                })
                .verify();

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void addChallengeToBookmarks_ShouldReturnTrue_WhenBookmarksIsNull() {
        UUID userId = UUID.randomUUID();
        UUID challengeId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(bookmarkRepository.existsByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(false));
        when(bookmarkRepository.save(any(BookmarkDocument.class)))
                .thenReturn(Mono.just(new BookmarkDocument()));

        StepVerifier.create(userService.addChallengeToBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(bookmarkRepository, times(1)).existsByUserIdAndChallengeId(userId, challengeId);
        verify(bookmarkRepository, times(1)).save(any(BookmarkDocument.class));
        verify(userRepository, never()).save(any());
    }

    @Test
    void addChallengeToBookmarks_ShouldReturnTrue_WhenBookmarksIsEmpty() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(bookmarkRepository.existsByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(false));
        when(bookmarkRepository.save(any(BookmarkDocument.class)))
                .thenReturn(Mono.just(new BookmarkDocument()));

        StepVerifier.create(userService.addChallengeToBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(bookmarkRepository, times(1)).existsByUserIdAndChallengeId(userId, challengeId);
        verify(bookmarkRepository, times(1)).save(any(BookmarkDocument.class));
    }

    @Test
    void addChallengeToBookmarks_ShouldReturnTrue_WhenBookmarksHasValues() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(bookmarkRepository.existsByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(false));
        when(bookmarkRepository.save(any(BookmarkDocument.class)))
                .thenReturn(Mono.just(new BookmarkDocument()));

        StepVerifier.create(userService.addChallengeToBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(bookmarkRepository, times(1)).existsByUserIdAndChallengeId(userId, challengeId);
        verify(bookmarkRepository, times(1)).save(any());
    }

    @Test
    void addChallengeToBookmarks_ShouldReturnFalse_WhenBookmarksAlreadyContainsChallenge() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(bookmarkRepository.existsByUserIdAndChallengeId(userId, challengeId))
                .thenReturn(Mono.just(true));

        StepVerifier.create(userService.addChallengeToBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(bookmarkRepository, times(1)).existsByUserIdAndChallengeId(userId, challengeId);
        verify(bookmarkRepository, never()).save(any());
    }

    @Test
    void addChallengeToBookmarks_ShouldThrowBadRequestException_WhenChallengeUuidIsNull() {
        StepVerifier.create(userService.addChallengeToBookmarks(UUID.randomUUID().toString(), null))
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
        StepVerifier.create(userService.addChallengeToBookmarks("invalidUuid", UUID.randomUUID().toString()))
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
        StepVerifier.create(userService.addChallengeToBookmarks(UUID.randomUUID().toString(), "invalidUuid"))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }


    @Test
    void deleteChallengeFromBookmarks_ShouldReturnFalse_WhenBookmarksIsNull() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(bookmarkRepository.findByUserIdAndChallengeId(any(UUID.class), any(UUID.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteChallengeFromBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(bookmarkRepository).findByUserIdAndChallengeId(any(UUID.class), any(UUID.class));
        verify(bookmarkRepository, never()).delete(any());
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldReturnFalse_WhenBookmarksIsEmpty() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(bookmarkRepository.findByUserIdAndChallengeId(any(UUID.class), any(UUID.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteChallengeFromBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(bookmarkRepository).findByUserIdAndChallengeId(any(UUID.class), any(UUID.class));
        verify(bookmarkRepository, never()).delete(any());
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldReturnFalse_WhenChallengeNotInBookmarks() {
        UUID challengeId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserDocument user = new UserDocument(userId, "testUser", null, 0);

        when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        when(bookmarkRepository.findByUserIdAndChallengeId(any(UUID.class), any(UUID.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteChallengeFromBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(false)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
        verify(bookmarkRepository).findByUserIdAndChallengeId(any(UUID.class), any(UUID.class));
        verify(bookmarkRepository, never()).delete(any());
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

        StepVerifier.create(userService.deleteChallengeFromBookmarks(userId.toString(), challengeId.toString()))
                .expectNext(true)
                .verifyComplete();

        verify(userRepository).findById(userId);
        verify(bookmarkRepository).findByUserIdAndChallengeId(userId, challengeId);
        verify(bookmarkRepository).delete(bookmark);
    }

    @Test
    void deleteChallengeFromBookmarks_ShouldThrowNotFoundException_WhenUserNotFound() {

        when(userRepository.findById(any(UUID.class))).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteChallengeFromBookmarks(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
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
        StepVerifier.create(userService.deleteChallengeFromBookmarks(null, UUID.randomUUID().toString()))
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
        StepVerifier.create(userService.deleteChallengeFromBookmarks(UUID.randomUUID().toString(), null))
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
        StepVerifier.create(userService.deleteChallengeFromBookmarks("invalidUuid", UUID.randomUUID().toString()))
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
        StepVerifier.create(userService.deleteChallengeFromBookmarks(UUID.randomUUID().toString(), "invalidUuid"))
                .expectErrorSatisfies(throwable -> {
                    assertInstanceOf(BadUUIDException.class, throwable);
                    assertEquals("Invalid ID format", throwable.getMessage());
                })
                .verify();

        verify(userRepository, times(0)).findById(any(UUID.class));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("getUserById returns the user when the user exists")
    void getUserById_ShouldReturnUser_WhenUserExists() {
        String username = "existingUser";
        UUID userId=UUID.randomUUID();
        UserDocument existingUser = new UserDocument(userId, username, Role.ADMIN, 0);
        when(userRepository.findById(userId)).thenReturn(Mono.just(existingUser));

        StepVerifier.create(userService.getUserById(userId.toString()))
                .expectNext(existingUser)
                .verifyComplete();

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("getUserById throws NotFoundException when the user does not exist")
    void getUserById_ShouldReturnNotFoundException_WhenUserDoesNotExist() {
        UUID userId=UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Mono.empty());

        StepVerifier.create(userService.getUserById(userId.toString()))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(NotFoundException.class, error);
                    assertEquals("User not found", error.getMessage());
                })
                .verify();
        verify(userRepository, times(1)).findById(userId);
    }
}
