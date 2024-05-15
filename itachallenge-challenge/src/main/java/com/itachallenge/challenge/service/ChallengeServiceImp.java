package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.document.SolutionDocument;
import com.itachallenge.challenge.dto.ChallengeDto;
import com.itachallenge.challenge.dto.GenericResultDto;
import com.itachallenge.challenge.dto.SolutionDto;
import com.itachallenge.challenge.dto.LanguageDto;
import com.itachallenge.challenge.dto.RelatedDto;
import com.itachallenge.challenge.exception.*;
import com.itachallenge.challenge.document.TestingValueDocument;
import com.itachallenge.challenge.dto.*;
import com.itachallenge.challenge.exception.BadUUIDException;
import com.itachallenge.challenge.exception.ChallengeNotFoundException;
import com.itachallenge.challenge.exception.ResourceNotFoundException;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
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

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ChallengeServiceImp implements IChallengeService {

    private static final Pattern UUID_FORM = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);

    private static final Logger log = LoggerFactory.getLogger(ChallengeServiceImp.class);

    private static final String CHALLENGE_NOT_FOUND_ERROR = "Challenge with id %s not found";

    private static final String LANGUAGE_NOT_FOUND = "Language with id %s not found";

    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private SolutionRepository solutionRepository;
    @Autowired
    private DocumentToDtoConverter<ChallengeDocument, ChallengeDto> challengeConverter = new DocumentToDtoConverter<>();
    @Autowired
    private DocumentToDtoConverter<LanguageDocument, LanguageDto> languageConverter = new DocumentToDtoConverter<>();
    @Autowired
    private DocumentToDtoConverter<SolutionDocument, SolutionDto> solutionConverter = new DocumentToDtoConverter<>();
    @Autowired
    private DocumentToDtoConverter<ChallengeDocument, RelatedDto> relatedChallengeConverter = new DocumentToDtoConverter<>();
    @Autowired
    private DocumentToDtoConverter<TestingValueDocument, TestingValueDto> testingValueConverter = new DocumentToDtoConverter<>();


    public Mono<ChallengeDto> getChallengeById(String id) {
        return validateUUID(id)
                .flatMap(challengeId -> challengeRepository.findByUuid(challengeId)
                        .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeId))))
                        .map(challenge -> challengeConverter.convertDocumentToDto(challenge, ChallengeDto.class))
                        .doOnSuccess(challengeDto -> log.info("Challenge found with ID: {}", challengeId))
                        .doOnError(error -> log.error("Error occurred while retrieving challenge: {}", error.getMessage()))
                );
    }

    public Mono<String> removeResourcesByUuid(String id) {
        return validateUUID(id)
                .flatMap(resourceId -> {
                    Flux<ChallengeDocument> challengesToUpdate = challengeRepository.findAllByResourcesContaining(resourceId);

                    return challengesToUpdate
                            .hasElements()
                            .flatMap(result -> {
                                if (Boolean.FALSE.equals(result)) {
                                    return Mono.error(new ResourceNotFoundException("Resource with id " + resourceId + " not found"));
                                }

                                return challengesToUpdate
                                        .flatMap(challenge -> {
                                            Set<UUID> updatedResources = new HashSet<>(challenge.getResources());
                                            updatedResources.remove(resourceId);
                                            challenge.setResources(updatedResources);
                                            return challengeRepository.save(challenge);
                                        })
                                        .then(Mono.just("Resource removed successfully"));
                            });
                })
                .doOnSuccess(resultDto -> log.info("Resource found with ID: {}", id))
                .doOnError(error -> log.error("Error occurred while retrieving resource: {}", error.getMessage()));
    }
    private Mono<ChallengeDocument> updateChallenge(ChallengeDocument challenge,UUID resourceId) {
        challenge.setResources(challenge.getResources().stream()
                .filter(s -> !s.equals(resourceId))
                .collect(Collectors.toSet()));
        return challengeRepository.save(challenge);
    }

    @Override
    public Mono<GenericResultDto<ChallengeDto>> getChallengesByLanguageOrDifficulty(Optional<String> idLanguage, Optional<String> level, int offset, int limit) {
        Flux<ChallengeDocument> challenges;

        if (idLanguage.isPresent() && level.isPresent()) {
            challenges = validateUUID(idLanguage.get())
                    .flatMapMany(uuid -> languageRepository.findByIdLanguage(uuid)
                            .switchIfEmpty(Mono.error(new NotFoundException(String.format(LANGUAGE_NOT_FOUND, idLanguage.get()))))
                            .flatMapMany(language -> challengeRepository.findByLevelAndLanguages_IdLanguage(level.get(), uuid)));
        } else if (idLanguage.isPresent()) {
            challenges = validateUUID(idLanguage.get())
                    .flatMapMany(uuid -> languageRepository.findByIdLanguage(uuid)
                            .switchIfEmpty(Mono.error(new NotFoundException(String.format(LANGUAGE_NOT_FOUND, idLanguage.get()))))
                            .flatMapMany(language -> challengeRepository.findByLanguages_IdLanguage(uuid)));
        } else if (level.isPresent()) {
            challenges = challengeRepository.findByLevel(level.get())
                    .switchIfEmpty(Mono.error(new NotFoundException("Level " + level.get() + " not found")));
        } else {
            challenges = challengeRepository.findAllByUuidNotNullExcludingTestingValues()
                    .switchIfEmpty(Mono.error(new ChallengeNotFoundException("No challenges found")));
        }

        Flux<ChallengeDocument> finalChallenges = challenges;
        return challenges.count().flatMap(total -> {
            Flux<ChallengeDocument> pagedChallenges = finalChallenges.skip(offset);
            if (limit != -1) {
                pagedChallenges = pagedChallenges.take(limit);
            }
            return pagedChallenges.map(challenge -> challengeConverter.convertDocumentToDto(challenge, ChallengeDto.class))
                    .collectList()
                    .map(challengeDtoList -> {
                        GenericResultDto<ChallengeDto> resultDto = new GenericResultDto<>();
                        resultDto.setInfo(offset, limit, total.intValue(), challengeDtoList.toArray(new ChallengeDto[0]));
                        return resultDto;
                    });
        });
    }

    public Mono<GenericResultDto<LanguageDto>> getAllLanguages() {
        Flux<LanguageDto> languagesDto = languageConverter.convertDocumentFluxToDtoFlux(languageRepository.findAll(), LanguageDto.class);
        return languagesDto.collectList().map(language -> {
            GenericResultDto<LanguageDto> resultDto = new GenericResultDto<>();
            resultDto.setInfo(0, language.size(), language.size(), language.toArray(new LanguageDto[0]));
            return resultDto;
        });
    }

    @Override
    public Flux<ChallengeDto> getAllChallenges(int offset, int limit) {

        return challengeConverter.convertDocumentFluxToDtoFlux(challengeRepository.findAllByUuidNotNullExcludingTestingValues().skip(offset).take(limit) , ChallengeDto.class);
    }

    public Mono<GenericResultDto<SolutionDto>> getSolutions(String idChallenge, String idLanguage, int offset, int limit) {
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
                                    .flatMap(solution -> Mono.from(solutionConverter.convertDocumentFluxToDtoFlux(Flux.just(solution), SolutionDto.class)))
                            )
                            .skip(offset)
                            .take(limit)
                            .collectList()
                            .map(solutions -> {
                                GenericResultDto<SolutionDto> resultDto = new GenericResultDto<>();
                                resultDto.setInfo(offset, limit, solutions.size(), solutions.toArray(new SolutionDto[0]));
                                return resultDto;
                            });
                });
    }


    public Mono<SolutionDto> addSolution(SolutionDto solutionDto) {

        Mono<UUID> challengeIdMono = validateUUID(String.valueOf(solutionDto.getIdChallenge()));
        Mono<UUID> languageIdMono = validateUUID(String.valueOf(solutionDto.getIdLanguage()));

        return Mono.zip(challengeIdMono, languageIdMono)
                .flatMap(tuple -> {
                    UUID challengeId = tuple.getT1();
                    UUID languageId = tuple.getT2();

                    return challengeRepository.findByUuid(challengeId)
                            .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeId))))

                            .flatMap(challenge -> {
                                SolutionDocument solutionDocument = new SolutionDocument();
                                solutionDocument.setSolutionText(solutionDto.getSolutionText());
                                solutionDocument.setIdLanguage(languageId);
                                solutionDocument.setUuid(UUID.randomUUID());

                                return solutionRepository.save(solutionDocument)
                                        .flatMap(solution -> {
                                            if (challenge.getSolutions() == null) {
                                                List<UUID> list = new ArrayList<>();
                                                challenge.setSolutions(list);
                                            }
                                            challenge.getSolutions().add(solution.getUuid());
                                            return challengeRepository.save(challenge);
                                        })
                                        .flatMap(challengeSaved ->
                                                Mono.from(solutionConverter.convertDocumentFluxToDtoFlux(Flux.just(solutionDocument),
                                                        SolutionDto.class)))
                                        .map(solution -> {
                                            GenericResultDto<SolutionDto> resultDto = new GenericResultDto<>();
                                            resultDto.setInfo(0, 1, 1, new SolutionDto[]{solution});
                                            solution.setIdChallenge(challengeId);
                                            return solution;
                                        });
                            });
                });

    }

    @Override
    public Mono<GenericResultDto<ChallengeDto>> getRelatedChallenges(String id, int offset, int limit) {

        return validateUUID(id)
                .flatMap(challengeId -> challengeRepository.findByUuid(challengeId)
                        .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeId))))
                        .flatMapMany(challenge -> Flux.fromIterable(challenge.getRelatedChallenges())
                                .flatMap(relatedChallengeId -> challengeRepository.findByUuid(relatedChallengeId))
                                .flatMap(relatedChallenge -> Mono.from(challengeConverter.convertDocumentFluxToDtoFlux(Flux.just(relatedChallenge), ChallengeDto.class)))
                        )
                        .skip(offset)
                        .take(limit)
                        .collectList()
                        .map(relatedChallenges -> {
                            GenericResultDto<ChallengeDto> resultDto = new GenericResultDto<>();
                            resultDto.setInfo(0, relatedChallenges.size(), relatedChallenges.size(), relatedChallenges.toArray(new ChallengeDto[0]));
                            return resultDto;
                        })
                );
    }
    @Override
    public Mono<Map<String, Object>> getTestingParamsByChallengeIdAndLanguageId(String idChallenge, String idLanguage) {

        Mono<UUID> challengeIdMono = validateUUID(idChallenge);
        Mono<UUID> languageIdMono = validateUUID(idLanguage);

        return Mono.zip(challengeIdMono, languageIdMono)
                .flatMap(tuple -> {
                    UUID challengeId = tuple.getT1();
                    UUID languageId = tuple.getT2();

                    return challengeRepository.findByUuid(challengeId)
                            .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeId))))
                            .flatMap(challenge -> {
                                Optional<LanguageDocument> languageOptional = challenge.getLanguages()
                                        .stream()
                                        .filter(lang -> lang.getIdLanguage().equals(languageId))
                                        .findFirst();
                                if (languageOptional.isPresent()) {
                                    return Flux.fromIterable(challenge.getTestingValues())
                                            .map(testingValueDocument -> testingValueConverter.convertDocumentToDto(testingValueDocument, TestingValueDto.class))
                                            .collectList()
                                            .map(testingValues -> {
                                                Map<String, Object> response = new LinkedHashMap<>();
                                                response.put("uuid_challenge", idChallenge);  // Changed from "challengeId" to "uuid_challenge"
                                                response.put("uuid_language", idLanguage);  // Changed from "languageId" to "uuid_language"
                                                response.put("test_params", testingValues);
                                                return response;
                                            });
                                } else {
                                    return Mono.error(new ChallengeNotFoundException("Language " + idLanguage + " not found in Challenge " + idChallenge));
                                }
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
