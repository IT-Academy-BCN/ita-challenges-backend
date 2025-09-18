package com.itachallenge.user.service;

import com.itachallenge.githubcore.document.enums.GithubUserStatus;
import com.itachallenge.githubcore.service.GithubApiService;
import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.document.enums.Role;
import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.exception.GithubUserNotFoundException;
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
    private final GithubApiService githubApiService;

    public AdminCreateUserService(UserRepository userRepository, GithubApiService githubApiService) {
        this.userRepository = userRepository;
        this.githubApiService = githubApiService;
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
                        githubApiService.userExists(username)
                                .flatMap(status -> {
                                    if (status == GithubUserStatus.NOT_FOUND) {
                                        log.warn("GitHub user '{}' does not exist", username);
                                        return Mono.error(new GithubUserNotFoundException("GitHub user not found: " + username));
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
                                .doOnError(e -> !(e instanceof UsernameAlreadyExistsException || e instanceof GithubUserNotFoundException), e ->
                                        log.error("An unexpected error occurred while creating user {}", username, e)
                                )
                ));
    }
}
