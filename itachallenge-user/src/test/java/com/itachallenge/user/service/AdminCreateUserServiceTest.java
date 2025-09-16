package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.githubcore.service.IGithubApiService;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.exception.GithubUserNotFoundException;
import com.itachallenge.user.exception.UsernameAlreadyExistsException;
import com.itachallenge.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCreateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private IGithubApiService githubApiService;

    @InjectMocks
    private AdminCreateUserService adminCreateUserService;

    @Test
    @DisplayName("Test: Create user when username does not exist in DB but in Github does")
    void createUser_whenUserDoesNotExistInDB_shouldCreateAndReturnUser() {

        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsername("newUser");

        UserDocument savedUser = UserDocument.builder()
                .uuid(UUID.randomUUID())
                .username("newUser")
                .role(Role.USER)
                .build();

        when(userRepository.findByUsername("newUser")).thenReturn(Mono.empty());
        when(githubApiService.userExists("newUser")).thenReturn(Mono.just(true));
        when(userRepository.save(any(UserDocument.class))).thenReturn(Mono.just(savedUser));

        Mono<AdminCreateUserResponseDto> result = adminCreateUserService.createUser(request);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getUsername().equals("newUser") &&
                        response.getUserId() != null)
                .verifyComplete();

        verify(userRepository).save(any(UserDocument.class));
        verify(githubApiService).userExists("newUser");
    }

    @Test
    @DisplayName("Test: Create user when username does not exist in DB but it's not a real Github username")
    void createUser_whenUserDoesNotExistInDBAndIsNotAGithubUser_shouldThrowException() {
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsername("newUser");

        when(userRepository.findByUsername("newUser")).thenReturn(Mono.empty());
        when(githubApiService.userExists("newUser")).thenReturn(Mono.just(false));

        Mono<AdminCreateUserResponseDto> result = adminCreateUserService.createUser(request);

        StepVerifier.create(result)
                .expectError(GithubUserNotFoundException.class)
                .verify();

        verify(userRepository, never()).save(any());
        verify(githubApiService).userExists("newUser");
    }

    @Test
    @DisplayName("Test: Attempt to create a user that already exists")
    void createUser_whenUserAlreadyExists_shouldThrowException() {
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsername("existingUser");

        UserDocument existingUser = new UserDocument();
        existingUser.setUsername("existingUser");

        when(userRepository.findByUsername("existingUser")).thenReturn(Mono.just(existingUser));

        Mono<AdminCreateUserResponseDto> result = adminCreateUserService.createUser(request);

        StepVerifier.create(result)
                .expectError(UsernameAlreadyExistsException.class)
                .verify();

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: GitHub API failure should propagate error")
    void createUser_whenGithubApiFails_shouldPropagateError() {
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsername("newUser");

        when(userRepository.findByUsername("newUser")).thenReturn(Mono.empty());
        when(githubApiService.userExists("newUser")).thenReturn(Mono.error(new RuntimeException("GitHub API error")));

        Mono<AdminCreateUserResponseDto> result = adminCreateUserService.createUser(request);

        StepVerifier.create(result)
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        ex.getMessage().contains("GitHub API error"))
                .verify();

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test: New user should always have default USER role")
    void createUser_shouldAssignDefaultUserRole() {
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsername("newUser");

        UserDocument savedUser = UserDocument.builder()
                .uuid(UUID.randomUUID())
                .username("newUser")
                .role(Role.USER)
                .build();

        when(userRepository.findByUsername("newUser")).thenReturn(Mono.empty());
        when(githubApiService.userExists("newUser")).thenReturn(Mono.just(true));
        when(userRepository.save(any(UserDocument.class))).thenReturn(Mono.just(savedUser));

        Mono<AdminCreateUserResponseDto> result = adminCreateUserService.createUser(request);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.getUsername().equals("newUser") &&
                        response.getUserId() != null)
                .verifyComplete();

        verify(userRepository).save(argThat(user -> user.getRole() == Role.USER));
    }
}