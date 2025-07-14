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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(AdminCreateUserController.class)
class AdminCreateUserControllerWebTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private IJwtService jwtService;

    @MockBean
    private AdminCreateUserService adminCreateUserService;

    @Test
    @DisplayName("Web Test: POST /users/create with a list of users should return 201 Created")
    void createUsers_validRequest_shouldReturn201() {

        String token = "Bearer mockAdminToken";
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsernames(List.of("newUser1", "existingUser"));

        AdminCreateUserResponseDto serviceResponse = AdminCreateUserResponseDto.builder()
                .createdUsers(List.of(new AdminCreateUserResponseDto.UserCreatedDto(UUID.randomUUID(), "newUser1")))
                .existingUsers(List.of("existingUser"))
                .build();

        when(jwtService.extractRoleFromToken(token)).thenReturn(Mono.just("ADMIN"));
        when(adminCreateUserService.createUsers(any())).thenReturn(Mono.just(serviceResponse));

        webTestClient.post()
                .uri("/itachallenge/api/v1/admin/users/create")
                .header("Authorization", token)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AdminCreateUserResponseDto.class)
                .value(response -> {
                    Assertions.assertEquals(1, response.getCreatedUsers().size());
                    Assertions.assertEquals(1, response.getExistingUsers().size());
                    Assertions.assertEquals("newUser1", response.getCreatedUsers().get(0).getUsername());
                });
    }
}