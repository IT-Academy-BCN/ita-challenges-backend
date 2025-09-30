package com.itachallenge.user.controller;

import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.service.IAdminCreateUserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/itachallenge/api/v1/admin")
public class AdminCreateUserController {

    private final IAdminCreateUserService adminCreateUserService;
    private final Logger log = LoggerFactory.getLogger(AdminCreateUserController.class);

    public AdminCreateUserController(IAdminCreateUserService adminCreateUserService) {
        this.adminCreateUserService = adminCreateUserService;
    }

    @PostMapping("/users/create")
    //TODO: it has no validation restriction at the moment, it will be added soon.
    public Mono<ResponseEntity<AdminCreateUserResponseDto>> createUser(
            @Valid @RequestBody AdminCreateUserRequestDto request) {
        return adminCreateUserService.createUser(request)
                .doOnNext(userDto -> log.info("User returned: {} with points {}", userDto.getUsername(), userDto.getPoints()))
                .map(userDto -> new ResponseEntity<>(userDto, HttpStatus.CREATED));
    }
}