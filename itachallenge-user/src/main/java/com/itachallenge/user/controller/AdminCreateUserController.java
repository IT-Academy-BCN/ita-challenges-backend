package com.itachallenge.user.controller;

import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.exception.UnauthorizedException;
import com.itachallenge.user.service.AdminCreateUserService;
import com.itachallenge.user.service.IJwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/itachallenge/api/v1/admin")
public class AdminCreateUserController {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String ROLE_ADMIN = "ADMIN";

    private final AdminCreateUserService adminCreateUserService;
    private final IJwtService jwtService;

    public AdminCreateUserController(AdminCreateUserService adminCreateUserService, IJwtService jwtService) {
        this.adminCreateUserService = adminCreateUserService;
        this.jwtService = jwtService;
    }

    @PostMapping("/users/create")
    public Mono<ResponseEntity<AdminCreateUserResponseDto>> createUser(
            @RequestHeader(AUTHORIZATION_HEADER) String token,
            @Valid @RequestBody AdminCreateUserRequestDto request) {

        return jwtService.extractRoleFromToken(token)
                .switchIfEmpty(Mono.error(new UnauthorizedException("Token is invalid or missing")))
                .flatMap(role -> {
                    if (!ROLE_ADMIN.equals(role)) {
                        return Mono.just(new ResponseEntity<>(HttpStatus.FORBIDDEN));
                    }
                    return adminCreateUserService.createUser(request)
                            .map(userDto -> new ResponseEntity<>(userDto, HttpStatus.CREATED));
                });
    }
}