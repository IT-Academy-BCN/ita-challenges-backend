package com.itachallenge.user.controller;

import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
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

import java.util.Collections;
import java.util.List;

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
    @DisplayName("Unit Test: Controller should call service and return its response for an ADMIN user")
    void createUsers_validAdminRequest_shouldReturnServiceResponse() {
        String token = "Bearer mockAdminToken";
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsernames(List.of("newUser1"));

        AdminCreateUserResponseDto serviceResponse = AdminCreateUserResponseDto.builder()
                .createdUsers(Collections.emptyList())
                .existingUsers(Collections.emptyList())
                .build();

        when(jwtService.extractRoleFromToken(token)).thenReturn(Mono.just("ADMIN"));
        when(adminCreateUserService.createUsers(any())).thenReturn(Mono.just(serviceResponse));

        Mono<ResponseEntity<AdminCreateUserResponseDto>> result =
                adminCreateUserController.createUsers(token, request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                    assertThat(response.getBody()).isEqualTo(serviceResponse);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Unit Test: Controller should return 403 Forbidden when role is not ADMIN")
    void createUsers_whenRoleIsNotAdmin_shouldReturnForbidden() {
        String token = "Bearer mockUserToken";
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsernames(Collections.singletonList("anyUser"));

        when(jwtService.extractRoleFromToken(token)).thenReturn(Mono.just("USER"));

        Mono<ResponseEntity<AdminCreateUserResponseDto>> result =
                adminCreateUserController.createUsers(token, request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
                })
                .verifyComplete();

        verify(adminCreateUserService, never()).createUsers(any());
    }
}