package com.itachallenge.user.service;

import com.itachallenge.githubcore.exception.GithubUnavailableException;
import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.exception.GithubUserNotFoundException;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.exception.UsernameAlreadyExistsException;
import com.itachallenge.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AdminCreateUserService implements IAdminCreateUserService {

    private static final Logger log = LoggerFactory.getLogger(AdminCreateUserService.class);

    private final UserRepository userRepository;
    private final IGithubApiService githubApiService;

    public AdminCreateUserService(UserRepository userRepository, IGithubApiService githubApiService) {
        this.userRepository = userRepository;
        this.githubApiService = githubApiService;
    }

    @Override
    public Mono<AdminCreateUserResponseDto> createUser(AdminCreateUserRequestDto request) {
        final String username = request.getUsername();
        log.info("Attempting to create user with username: {}", username);

        return userRepository.findByUsername(request.getUsername())
                .flatMap(existing -> Mono.<AdminCreateUserResponseDto>error(new UsernameAlreadyExistsException(username)))
                .switchIfEmpty(
                        githubApiService.userExists(username)
                                .flatMap(exists -> {
                                    if (!exists) {
                                        return Mono.error(new RuntimeException("GitHub user does not exist"));
                                    }

                    UserDocument newUser = UserDocument.builder()
                            .uuid(UUID.randomUUID())
                            .username(username)
                            .role(Role.USER)
                            .build();

                    return userRepository.save(newUser)
                            .map(savedUser -> AdminCreateUserResponseDto.builder()
                                    .userId(savedUser.getUuid().toString())
                                    .username(savedUser.getUsername())
                                    .role(savedUser.getRole().toString())
                                    .build());
                }))

                .doOnSuccess(responseDto ->
                        log.info("Successfully created user '{}'", responseDto.getUsername())
                )
                .doOnError(UsernameAlreadyExistsException.class, e ->
                        log.warn("Attempt to create a user that already exists: {}", username)
                )
                .doOnError(e -> !(e instanceof UsernameAlreadyExistsException), e ->
                        log.error("An unexpected error occurred while creating user {}", username, e)
                );
    }
}