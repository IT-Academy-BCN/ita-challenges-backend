package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void isMentor_ShouldReturnTrue_WhenUserExists() {
        String username = "mentorUser";
        when(userRepository.existsByUsername(username)).thenReturn(Mono.just(true));

        StepVerifier.create(userService.isMentor(Mono.just(username)))
                .expectNext(true)
                .verifyComplete();

        verify(userRepository, times(1)).existsByUsername(username);
    }

    @Test
    void isMentor_ShouldReturnFalse_WhenUserDoesNotExist() {
        String username = "nonExistentUser";
        when(userRepository.existsByUsername(username)).thenReturn(Mono.just(false));

        StepVerifier.create(userService.isMentor(Mono.just(username)))
                .expectNext(false)
                .verifyComplete();

        verify(userRepository, times(1)).existsByUsername(username);
    }

    @Test
    void getUser_ShouldReturnUser_WhenUserExists() {
        String username = "existingUser";
        UserDocument existingUser = new UserDocument(UUID.randomUUID(), username, Role.ADMIN);
        when(userRepository.findByUsername(username)).thenReturn(Mono.just(existingUser));

        StepVerifier.create(userService.getUser(Mono.just(username)))
                .expectNext(existingUser)
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void getUser_ShouldReturnEmptyMono_WhenUserDoesNotExist() {
        String username = "nonExistentUser";
        when(userRepository.findByUsername(username)).thenReturn(Mono.empty());

        StepVerifier.create(userService.getUser(Mono.just(username)))
                .expectNextCount(0)
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(username);
    }
}
