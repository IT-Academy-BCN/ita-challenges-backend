package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionUser;
import com.itachallenge.user.dtos.SolutionUserDto;
import com.itachallenge.user.exceptions.BadUUIDException;
import com.itachallenge.user.exceptions.SolutionUserCreationException;
import com.itachallenge.user.helper.Converter;
import com.itachallenge.user.repository.SolutionUserRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class SolutionUserServiceImpl implements SolutionService{

    private static final Pattern UUID_FORM = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);
    private static final Logger log = LoggerFactory.getLogger(SolutionUserServiceImpl.class);
    @Autowired
    private SolutionUserRepository solutionUserRepository;
    @Autowired
    private Converter converter;

    @Override
    public Mono<SolutionUserDto> createUserSolution(UUID idUser, UUID idChallenge, UUID idLanguage, String solutionText) {
        Mono<UUID> validIdUser = validateUUID(idUser);
        Mono<UUID> validIdChallenge = validateUUID(idChallenge);
        Mono<UUID> validIdLanguage = validateUUID(idLanguage);

        return Mono.zip(validIdUser, validIdChallenge, validIdLanguage)
                .flatMap(tuple -> {
                    UUID userId = tuple.getT1();
                    UUID challengeId = tuple.getT2();
                    UUID languageId = tuple.getT3();

                    SolutionUser solutionUser = SolutionUser.builder()
                            .uuid_solutionUser(UUID.randomUUID())
                            .uuid_user(userId)
                            .uuid_challenge(challengeId)
                            .uuid_language(languageId)
                            .solution_Text(solutionText)
                            .build();

                    log.info("SolutionUser created successfully");

                    return Mono.fromCallable(() -> solutionUserRepository.save(solutionUser))
                            .flatMap(savedSolution -> {
                                Mono<SolutionUserDto> solutionUserDtoMono = converter.fromSolutionUserToSolutionUserDto(savedSolution);
                                return solutionUserDtoMono.switchIfEmpty(
                                        Mono.error(new SolutionUserCreationException("Conversion from SolutionUser to SolutionUserDto failed."))
                                );
                            })
                            .doOnSuccess(savedDto -> {
                                log.info("SolutionUser transition successfully: " + savedDto);
                            })
                            .onErrorResume(throwable -> {
                                log.error("Error saving SolutionUser. ", throwable);
                                return Mono.error(new SolutionUserCreationException("The user's Solution could not be saved."));
                            });
                });
    }

    private Mono<UUID> validateUUID(UUID id) {
        String validarUUID = String.valueOf(id);
        boolean validUUID = !StringUtils.isEmpty(validarUUID) && UUID_FORM.matcher(validarUUID).matches();
        if (!validUUID) {
            log.warn("Invalid ID format: {}", id);
            return Mono.error(new BadUUIDException("Invalid ID format. Please indicate the correct format."));
        }
        log.info("Valid ID format");
        return Mono.just(id);
    }

}
