package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.exception.BadUUIDException;
import com.itachallenge.user.exception.NotFoundException;
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
