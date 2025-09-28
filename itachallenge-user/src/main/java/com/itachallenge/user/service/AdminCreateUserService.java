package com.itachallenge.user.service;

import com.itachallenge.githubcore.exception.GithubUnavailableException;
import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.exception.NotFoundException;
import com.itachallenge.user.exception.UsernameAlreadyExistsException;
import com.itachallenge.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AdminCreateUserService implements IAdminCreateUserService {

    private static final Logger log = LoggerFactory.getLogger(AdminCreateUserService.class);

    private final UserRepository userRepository;
    private final ExternalGithubService externalGithubService;

    public AdminCreateUserService(UserRepository userRepository, ExternalGithubService externalGithubService) {
        this.userRepository = userRepository;
        this.externalGithubService = externalGithubService;
    }

    @Override
    public Mono<AdminCreateUserResponseDto> createUser(AdminCreateUserRequestDto request) {
        final String username = request.getUsername();
        log.info("Attempting to create user with username: {}", username);

        return userRepository.findByUsername(username)
                .flatMap(existing -> {
                    log.warn("Attempt to create a user that already exists: {}", username);
                    return Mono.<AdminCreateUserResponseDto>error(new UsernameAlreadyExistsException(username));
                })
                .switchIfEmpty(Mono.defer(() ->
                        externalGithubService.userExists(username)
                                .timeout(Duration.ofSeconds(3))
                                .onErrorMap(throwable -> {
                                    if (throwable instanceof java.util.concurrent.TimeoutException) {
                                        return new GithubUnavailableException("timeout");
                                    } else {
                                        return new GithubUnavailableException(throwable.getMessage());
                                    }
                                })
                                .flatMap(exists -> {
                                    if (!exists) {
                                        log.warn("GitHub user '{}' does not exist", username);
                                        return Mono.error(new NotFoundException("GitHub user not found: " + username));
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
                                                    .build())
                                            .doOnSuccess(responseDto ->
                                                    log.info("Successfully created user '{}'", responseDto.getUsername()));
                                })
                ));
    }
}
