package com.itachallenge.user.repository;

import com.itachallenge.user.document.UserDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private UUID uuid;
    private String username;
    private UserDocument userDocument;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        username = "testUser";
        userDocument = new UserDocument(uuid, username);
    }

    @Test
    void findByUuid() {
        when(userRepository.findByUuid(uuid)).thenReturn(Mono.just(userDocument));

        StepVerifier.create(userRepository.findByUuid(uuid))
                .expectNext(userDocument)
                .verifyComplete();

        verify(userRepository, times(1)).findByUuid(uuid);
    }

    @Test
    void findByUuidNotFound() {
        when(userRepository.findByUuid(uuid)).thenReturn(Mono.empty());

        StepVerifier.create(userRepository.findByUuid(uuid))
                .expectNextCount(0)
                .verifyComplete();

        verify(userRepository, times(1)).findByUuid(uuid);
    }

    @Test
    void findByUsername() {
        when(userRepository.findByUsername(username)).thenReturn(Mono.just(userDocument));

        StepVerifier.create(userRepository.findByUsername(username))
                .expectNext(userDocument)
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void findByUsernameNotFound() {
        when(userRepository.findByUsername(username)).thenReturn(Mono.empty());

        StepVerifier.create(userRepository.findByUsername(username))
                .expectNextCount(0)
                .verifyComplete();

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void existsByUuid() {
        when(userRepository.existsByUuid(uuid)).thenReturn(Mono.just(true));

        StepVerifier.create(userRepository.existsByUuid(uuid))
                .expectNext(true)
                .verifyComplete();

        verify(userRepository, times(1)).existsByUuid(uuid);
    }

    @Test
    void existsByUuidNotFound() {
        when(userRepository.existsByUuid(uuid)).thenReturn(Mono.just(false));

        StepVerifier.create(userRepository.existsByUuid(uuid))
                .expectNext(false)
                .verifyComplete();

        verify(userRepository, times(1)).existsByUuid(uuid);
    }
}
