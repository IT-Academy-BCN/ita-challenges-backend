package com.itachallenge.user.controller;

import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.exception.UsernameAlreadyExistsException;
import com.itachallenge.user.service.AdminCreateUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * ⚠️ AdminCreateUserControllerExceptionPathTest
 * Focus: Validation, conflicts, and error-handling responses.
 */
@WebMvcTest(controllers = AdminCreateUserController.class)
@ContextConfiguration(classes = { AdminCreateUserController.class })
@ComponentScan(basePackages = {
        "com.itachallenge.errorcore",
        "com.itachallenge.user.exception"
})
@ActiveProfiles("test")
class AdminCreateUserControllerExceptionPathTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AdminCreateUserService adminCreateUserService;

    @Test
    @DisplayName("POST /admin/users/create with existing user → 409 Conflict (UsernameAlreadyExistsException)")
    void createUser_withExistingUser_shouldReturn409Conflict() {
        // Arrange
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsername("existingUser");

        when(adminCreateUserService.createUser(any(AdminCreateUserRequestDto.class)))
                .thenReturn(Mono.error(new UsernameAlreadyExistsException("existingUser")));

        // Act & Assert
        webTestClient.post()
                .uri("/itachallenge/api/v1/admin/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.message").isEqualTo("The username 'existingUser' is already registered.");
    }
}
