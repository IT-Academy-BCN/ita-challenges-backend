package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.ChallengeDocument;
import com.itachallenge.challenge.document.LanguageDocument;
import com.itachallenge.challenge.document.SolutionDocument;
import com.itachallenge.challenge.dto.*;
import com.itachallenge.challenge.exception.BadUUIDException;
import com.itachallenge.challenge.exception.ChallengeNotFoundException;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.repository.LanguageRepository;
import com.itachallenge.challenge.repository.SolutionRepository;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ChallengeServiceImp implements IChallengeService {

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
    private DocumentToDtoConverter<ChallengeDocument, ChallengeDto> challengeConverter = new DocumentToDtoConverter<>();
    @Autowired
    private DocumentToDtoConverter<LanguageDocument, LanguageDto> languageConverter = new DocumentToDtoConverter<>();
    @Autowired
    private DocumentToDtoConverter<SolutionDocument, SolutionDto> solutionConverter = new DocumentToDtoConverter<>();
    @Autowired
    private DocumentToDtoConverter<ChallengeDocument, RelatedDto> relatedChallengeConverter = new DocumentToDtoConverter<>();


    public Mono<GenericResultDto<ChallengeDto>> getChallengeById(String id) {
        return validateUUID(id)
                .flatMap(challengeId -> challengeRepository.findByUuid(challengeId)
                        .flatMap(challenge -> Mono.from(challengeConverter.convertDocumentFluxToDtoFlux(Flux.just(challenge), ChallengeDto.class)))
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


    public Mono<GenericResultDto<ChallengeDto>> getChallengesByLanguageAndDifficulty(String idLanguage, String difficulty) {
        // TODO: Get challenges by languange and difficulty
        return null;
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
        return challengeConverter.convertDocumentFluxToDtoFlux(challengeRepository.findAllByUuidNotNull().skip(offset).take(limit) , ChallengeDto.class);    }

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
                                    .flatMap(solution -> Mono.from(solutionConverter.convertDocumentFluxToDtoFlux(Flux.just(solution), SolutionDto.class)))
                            )
                            .collectList()
                            .map(solutions -> {
                                GenericResultDto<SolutionDto> resultDto = new GenericResultDto<>();
                                resultDto.setInfo(0, solutions.size(), solutions.size(), solutions.toArray(new SolutionDto[0]));
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
    public Mono<GenericResultDto<ChallengeDto>> getRelatedChallenges(String id) {
        return validateUUID(id)
                .flatMap(challengeId -> challengeRepository.findByUuid(challengeId)
                        .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeId))))
                        .flatMapMany(challenge -> Flux.fromIterable(challenge.getRelatedChallenges())
                                .flatMap(relatedChallengeId -> challengeRepository.findByUuid(relatedChallengeId))
                                .flatMap(relatedChallenge -> Mono.from(challengeConverter.convertDocumentFluxToDtoFlux(Flux.just(relatedChallenge), ChallengeDto.class)))
                        )
                        .collectList()
                        .map(relatedChallenges -> {
                            GenericResultDto<ChallengeDto> resultDto = new GenericResultDto<>();
                            resultDto.setInfo(0, relatedChallenges.size(), relatedChallenges.size(), relatedChallenges.toArray(new ChallengeDto[0]));
                            return resultDto;
                        })
                );
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
