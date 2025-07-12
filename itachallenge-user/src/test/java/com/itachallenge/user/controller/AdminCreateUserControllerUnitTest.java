package com.itachallenge.user.controller;

import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.exception.UsernameAlreadyExistsException;
import com.itachallenge.user.service.AdminCreateUserService;
import com.itachallenge.user.service.IJwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCreateUserControllerUnitTest {

    @Mock
    private IJwtService jwtService;

    @Mock
    private AdminCreateUserService adminCreateUserService;

    @InjectMocks
    private AdminCreateUserController adminCreateUserController;

    @Test
    @DisplayName("Unit Test: Controller should propagate error when user already exists")
    void createUser_existingUsername_shouldPropagateError() {

        String token = "Bearer mockAdminToken";
        String username = "existingUser";
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsername(username);

        String errorMessage = "The username '" + username + "' is already registered.";

        when(jwtService.extractRoleFromToken(token)).thenReturn(Mono.just("ADMIN"));
        when(adminCreateUserService.createUser(any()))
                .thenReturn(Mono.error(new UsernameAlreadyExistsException(errorMessage)));

        Mono<ResponseEntity<AdminCreateUserResponseDto>> result =
                adminCreateUserController.createUser(token, request);

        StepVerifier.create(result)
                .expectError(UsernameAlreadyExistsException.class)
                .verify();
    }

    @Test
    @DisplayName("Unit Test: Create user when role is not ADMIN should return 403 Forbidden")
    void createUser_whenRoleIsNotAdmin_shouldReturnForbidden() {
        String token = "Bearer mockUserToken";
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsername("anyUser");

        when(jwtService.extractRoleFromToken(token)).thenReturn(Mono.just("USER"));

        Mono<ResponseEntity<AdminCreateUserResponseDto>> result =
                adminCreateUserController.createUser(token, request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                })
                .verifyComplete();

        verify(adminCreateUserService, never()).createUser(any());
    }
}