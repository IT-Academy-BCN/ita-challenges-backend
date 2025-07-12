package com.itachallenge.user.controller;

import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.service.AdminCreateUserService;
import com.itachallenge.user.service.IJwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AdminCreateUserController.class,
        excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class AdminCreateUserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IJwtService jwtService;

    @MockBean
    private AdminCreateUserService adminCreateUserService;

    @Test
    @DisplayName("Test: POST /users/create with new user should return 201 Created")
    void createUser_newUsername_returns201() {
        String token = "Bearer mockAdminToken";
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsername("newUser");

        AdminCreateUserResponseDto serviceResponse = AdminCreateUserResponseDto.builder()
                .uuid(UUID.randomUUID().toString())
                .username("newUser")
                .role("USER")
                .build();

        when(jwtService.extractRoleFromToken(token)).thenReturn(Mono.just("ADMIN"));
        when(adminCreateUserService.createUser(any())).thenReturn(Mono.just(serviceResponse));

        webTestClient.post()
                .uri("/itachallenge/api/v1/admin/users/create")
                .header("Authorization", token)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AdminCreateUserResponseDto.class)
                .value(response -> Assertions.assertEquals("newUser", response.getUsername()));
    }
}