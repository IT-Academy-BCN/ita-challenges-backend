package com.itachallenge.user.controller;

import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.service.AdminCreateUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/itachallenge/api/v1/admin")
public class AdminCreateUserController {

    private final AdminCreateUserService adminCreateUserService;
    public AdminCreateUserController(AdminCreateUserService adminCreateUserService) {
        this.adminCreateUserService = adminCreateUserService;
    }

    @PostMapping("/users/create")
    public Mono<ResponseEntity<AdminCreateUserResponseDto>> createUser(
            @Valid @RequestBody AdminCreateUserRequestDto request) {
        return adminCreateUserService.createUser(request)
                .map(userDto -> new ResponseEntity<>(userDto, HttpStatus.CREATED));
    }
}