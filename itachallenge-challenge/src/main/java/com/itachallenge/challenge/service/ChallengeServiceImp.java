package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.SolutionDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.exception.BadUUIDException;
import com.itachallenge.challenge.exception.ChallengeNotFoundException;
import com.itachallenge.challenge.helper.Converter;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.repository.SolutionRepository;
import com.itachallenge.challenge.repository.LanguageRepository;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class ChallengeServiceImp implements IChallengeService {
    //VARIABLES
    private static final Pattern UUID_FORM = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);

    private static final Logger log = LoggerFactory.getLogger(ChallengeServiceImp.class);

    private static final String CHALLENGE_NOT_FOUND_ERROR = "Challenge with id %s not found";

    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private SolutionRepository solutionRepository;
    @Autowired
    private Converter converter;


    public Mono<GenericResultDto<ChallengeDto>> getChallengeById(String id) {
        return validateUUID(id)
                .flatMap(challengeId -> challengeRepository.findByUuid(challengeId)
                        .flatMap(challenge -> Mono.from(converter.fromChallengeToChallengeDto(Flux.just(challenge))))
                        .map(challengeDto -> {
                            GenericResultDto<ChallengeDto> resultDto = new GenericResultDto<>();
                            resultDto.setInfo(0, 1, 1, new ChallengeDto[]{challengeDto});
                            return resultDto;
                        })
                        .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeId))))
                        .doOnSuccess(resultDto -> log.info("Challenge found with ID: {}", challengeId))
                        .doOnError(error -> log.error("Error occurred while retrieving challenge: {}", error.getMessage()))
                );
    }

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

    public Mono<GenericResultDto<ChallengeDto>> getAllChallenges() {
        Flux<ChallengeDto> challengeDtoFlux = converter.fromChallengeToChallengeDto(challengeRepository.findAll());

        return challengeDtoFlux.collectList().map(challenges -> {
            GenericResultDto<ChallengeDto> resultDto = new GenericResultDto<>();
            resultDto.setInfo(0, challenges.size(), challenges.size(), challenges.toArray(new ChallengeDto[0]));
            return resultDto;
        });
    }

    public Mono<GenericResultDto<LanguageDto>> getAllLanguages() {
        Flux<LanguageDto> languagesDto = converter.fromLanguageToLanguageDto(languageRepository.findAll());
        return languagesDto.collectList().map(language -> {
            GenericResultDto<LanguageDto> resultDto = new GenericResultDto<>();
            resultDto.setInfo(0, language.size(), language.size(), language.toArray(new LanguageDto[0]));
            return resultDto;
        });
    }

    public Mono<GenericResultDto<SolutionDto>> getSolutions(String idChallenge, String idLanguage) {
        Mono<UUID> challengeIdMono = validateUUID(idChallenge);
        Mono<UUID> languageIdMono = validateUUID(idLanguage);

        return Mono.zip(challengeIdMono, languageIdMono)
                .flatMap(tuple -> {
                    UUID challengeId = tuple.getT1();
                    UUID languageId = tuple.getT2();

                    return challengeRepository.findByUuid(challengeId)
                            .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeId))))
                            .flatMapMany(challenge -> Flux.fromIterable(challenge.getSolutions())
                                    .flatMap(solutionId -> solutionRepository.findById(solutionId))
                                    .filter(solution -> solution.getIdLanguage().equals(languageId))
                                    .flatMap(solution -> Mono.from(converter.fromSolutionToSolutionDto(Flux.just(solution))))
                            )
                            .collectList()
                            .map(solutions -> {
                                GenericResultDto<SolutionDto> resultDto = new GenericResultDto<>();
                                resultDto.setInfo(0, solutions.size(), solutions.size(), solutions.toArray(new SolutionDto[0]));
                                return resultDto;
                            });
                });
    }

    private Mono<UUID> validateUUID(String id) {
        boolean validUUID = !StringUtils.isEmpty(id) && UUID_FORM.matcher(id).matches();

        if (!validUUID) {
            log.warn("Invalid ID format: {}", id);
            return Mono.error(new BadUUIDException("Invalid ID format. Please indicate the correct format."));
        }

        return Mono.just(UUID.fromString(id));
    }

}
