package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.exception.UsernameAlreadyExistsException;
import com.itachallenge.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
public class AdminCreateUserService {

    private final UserRepository userRepository;

    public AdminCreateUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<AdminCreateUserResponseDto> createUser(AdminCreateUserRequestDto request) {
        final String username = request.getUsername();

        return userRepository.findByUsername(username)
                .flatMap(existingUser ->
                        Mono.<AdminCreateUserResponseDto>error(new UsernameAlreadyExistsException("Username " + username + " already exists."))
                )
                .switchIfEmpty(Mono.defer(() -> {
                    UserDocument newUser = UserDocument.builder()
                            .uuid(UUID.randomUUID())
                            .username(username)
                            .role(Role.USER)
                            .build();

                    return userRepository.save(newUser)
                            .map(savedUser -> AdminCreateUserResponseDto.builder()
                                    .uuid(savedUser.getUuid().toString())
                                    .username(savedUser.getUsername())
                                    .role(savedUser.getRole().toString())
                                    .build());
                }));
    }
}