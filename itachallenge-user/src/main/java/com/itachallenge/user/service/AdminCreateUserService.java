package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AdminCreateUserService {

    private final UserRepository userRepository;

    public AdminCreateUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<AdminCreateUserResponseDto> createUsers(AdminCreateUserRequestDto request) {
        List<AdminCreateUserResponseDto.UserCreatedDto> createdUsers = new ArrayList<>();
        List<String> existingUsers = new ArrayList<>();

        return Flux.fromIterable(request.getUsernames())
                .concatMap(username ->
                        userRepository.findByUsername(username)
                                .hasElement()
                                .flatMap(exists -> {
                                    if (Boolean.TRUE.equals(exists)){
                                        existingUsers.add(username);
                                        return Mono.empty();
                                    } else {
                                        UserDocument newUser = UserDocument.builder()
                                                .uuid(UUID.randomUUID())
                                                .username(username)
                                                .role(Role.USER)
                                                .build();
                                        return userRepository.save(newUser);
                                    }
                                })
                )
                .doOnNext(savedUser ->
                        createdUsers.add(AdminCreateUserResponseDto.UserCreatedDto.builder()
                                .uuid(savedUser.getUuid())
                                .username(savedUser.getUsername())
                                .build()))
                .then(Mono.fromCallable(() ->
                        AdminCreateUserResponseDto.builder()
                                .createdUsers(createdUsers)
                                .existingUsers(existingUsers)
                                .build()
                ));
    }
}