package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.exception.BadUUIDException;
import com.itachallenge.challenge.exception.ChallengeNotFoundException;
import com.itachallenge.challenge.helper.Converter;
import com.itachallenge.challenge.repository.ChallengeRepository;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ChallengeServiceImp implements IChallengeService {
    //region VARIABLES
    private static final Pattern UUID_FORM = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);
    private static final Logger log = LoggerFactory.getLogger(ChallengeServiceImp.class);

    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private Converter converter;

    //endregion VARIABLES

    //region METHODS: Public
    @Override
    public Mono<GenericResultDto<ChallengeDto>> getAllChallenges() {
        Flux<ChallengeDto> challengeDtoFlux = converter.fromChallengeToChallengeDto(challengeRepository.findAll());

        return challengeDtoFlux.collectList().map(challenges -> {
            GenericResultDto<ChallengeDto> resultDto = new GenericResultDto<>();
            resultDto.setInfo(0, challenges.size(), challenges.size(), challenges.toArray(new ChallengeDto[0]));
            return resultDto;
        });
    }

    @Override
    public Mono<GenericResultDto<ChallengeDto>> getChallengeById(String id) {
        return validateUUID(id)
                .flatMap(challengeId -> challengeRepository.findByUuid(challengeId)
                        .flatMap(challenge -> Mono.from(converter.fromChallengeToChallengeDto(Flux.just(challenge))))
                        .map(challengeDto -> {
                            GenericResultDto<ChallengeDto> resultDto = new GenericResultDto<>();
                            resultDto.setInfo(0, 1, 1, new ChallengeDto[]{challengeDto});
                            return resultDto;
                        })
                        .switchIfEmpty(Mono.error(new ChallengeNotFoundException("Challenge with id " + challengeId + " not found")))
                        .doOnSuccess(resultDto -> log.info("Challenge found with ID: {}", challengeId))
                        .doOnError(error -> log.error("Error occurred while retrieving challenge: {}", error.getMessage()))
                );
    }

    @Override
    public Mono<GenericResultDto<ChallengeDto>> getChallengesByLanguagesAndLevel(Set<String> language, Set<String> level) {
        Flux<ChallengeDto> challengeDtoFlux = Optional.ofNullable(level)
                .filter(challengeLevel -> !challengeLevel.isEmpty())
                .map(challengeLevel -> converter.fromChallengeToChallengeDto(challengeRepository.findByLevelIn(challengeLevel)))
                .orElseGet(() -> Optional.ofNullable(language)
                        .filter(challengeLanguage -> !challengeLanguage.isEmpty())
                        .map(challengeLanguage -> converter.fromChallengeToChallengeDto(challengeRepository.findByLanguages_LanguageNameIn(challengeLanguage)))
                        .orElse(converter.fromChallengeToChallengeDto(challengeRepository.findAll())));
        // OUT
        return challengeDtoFlux
                .doOnNext(challenge -> log.info("Retrieved challenge: {}", challenge.getTitle()))
                .collectList()
                .doOnTerminate(() -> log.info("Challenges retrieval completed."))
                .map(challenges -> {
                    if (challenges.isEmpty()) {
                        throw new ChallengeNotFoundException("No challenges found for the given filters.");
                    } else {
                        log.info("Challenges retrieved successfully!");
                    }

                    GenericResultDto<ChallengeDto> resultDto = new GenericResultDto<>();
                    resultDto.setInfo(0, 5, challenges.size(), challenges.toArray(new ChallengeDto[0]));
                    return resultDto;
                })
                .onErrorResume(error -> {
                    log.error("Error occurred while retrieving challenges: {}", error.getMessage());
                    return Mono.error(error);
                });
    }

    @Override
    public Mono<GenericResultDto<String>> removeResourcesByUuid(String id) {
        return validateUUID(id)
                .flatMap(resourceId -> {
                    Flux<ChallengeDocument> challengeFlux = challengeRepository.findAllByResourcesContaining(resourceId);
                    return challengeFlux
                            .flatMap(challenge -> {
                                challenge.setResources(challenge.getResources().stream()
                                        .filter(s -> !s.equals(resourceId))
                                        .collect(Collectors.toSet()));
                                return challengeRepository.save(challenge);
                            })
                            .hasElements()
                            .flatMap(result -> {
                                if (Boolean.TRUE.equals(result)) {
                                    GenericResultDto<String> resultDto = new GenericResultDto<>();
                                    resultDto.setInfo(0, 1, 1, new String[]{"resource deleted correctly"});
                                    return Mono.just(resultDto);
                                } else {
                                    return Mono.error(new ChallengeNotFoundException("Resource with id " + resourceId + " not found"));
                                }
                            })
                            .doOnSuccess(resultDto -> log.info("Resource found with ID: {}", resourceId))
                            .doOnError(error -> log.error("Error occurred while retrieving resource: {}", error.getMessage()));
                });
    }

    //endregion METHODS: Public

    //region METHODS: Private
    private Mono<UUID> validateUUID(String id) {
        boolean validUUID = !StringUtils.isEmpty(id) && UUID_FORM.matcher(id).matches();

        if (!validUUID) {
            log.warn("Invalid ID format: {}", id);
            return Mono.error(new BadUUIDException("Invalid ID format. Please indicate the correct format."));
        }

        return Mono.just(UUID.fromString(id));
    }

    //endregion METHODS: Private

}
