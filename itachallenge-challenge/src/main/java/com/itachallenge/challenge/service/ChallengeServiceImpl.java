package com.itachallenge.challenge.service;

import com.itachallenge.challenge.document.*;
import com.itachallenge.challenge.dto.*;
import com.itachallenge.challenge.enums.Topic;
import com.itachallenge.challenge.exception.*;
import com.itachallenge.challenge.helper.DocumentToDtoConverter;
import com.itachallenge.challenge.repository.ChallengeRepository;
import com.itachallenge.challenge.repository.SolutionRepository;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;


@Service
public class ChallengeServiceImpl implements IChallengeService {

    private static final Pattern UUID_FORM = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);

    private static final Logger log = LoggerFactory.getLogger(ChallengeServiceImpl.class);

    private static final String CHALLENGE_NOT_FOUND_ERROR = "Challenge with id: %s not found";

    private static final String LANGUAGE_NOT_FOUND_ERROR = "Language with id: %s not found";

    private static final String NOT_FOUND = "not found";

    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private ILanguageService ILanguageService;
    @Autowired
    private SolutionRepository solutionRepository;
    @Autowired
    private DocumentToDtoConverter<ChallengeDocument, ChallengeDto> challengeConverter = new DocumentToDtoConverter<>();
    @Autowired
    private DocumentToDtoConverter<SolutionDocument, SolutionDto> solutionConverter = new DocumentToDtoConverter<>();
    @Autowired
    private IUserService userService;
    @Autowired
    private ITagService tagService;

    @Cacheable(value = "challenges", key = "#id", unless = "#result==null")
    public Mono<ChallengeDto> getChallengeById(String id) {
        return validateUUID(id)
                .flatMap(challengeId -> challengeRepository.findByUuid(challengeId)
                        .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeId))))
                        .map(challenge -> challengeConverter.convertDocumentToDto(challenge, ChallengeDto.class))
                        .doOnSuccess(challengeDto -> log.info("Challenge found with ID: {}", challengeId))
                        .doOnError(error -> log.error("Error occurred while retrieving challenge: {}", error.getMessage()))
                );
    }


    @Override
    public Flux<GenericResultDto<ChallengeDto>> getChallengesByFilter(
            Optional<String> idLanguage,
            Optional<String> level,
            Optional<List<UUID>> tags,
            int offset,
            int limit) {

        Optional<UUID> uuidLanguage = idLanguage
                .filter(lang -> !lang.isBlank())
                .map(UUID::fromString);

        boolean filterByLevel = level.isPresent() && !level.get().isBlank();

        return challengeRepository.findAllByUuidNotNullExcludingTestingValues()
                .filter(challenge ->
                        uuidLanguage.isEmpty() ||
                                (challenge.getLanguages() != null &&
                                        challenge.getLanguages().stream()
                                                .anyMatch(lang ->
                                                        lang.getIdLanguage() != null &&
                                                                lang.getIdLanguage().equals(uuidLanguage.get()))
                                )
                )
                .filter(challenge ->
                        !filterByLevel || level.get().equalsIgnoreCase(challenge.getLevel())
                )
                .filter(challenge ->
                        tags.isEmpty() || (
                                challenge.getTags() != null &&
                                        challenge.getTags().stream().anyMatch(tags.get()::contains)
                        )
                )
                .skip(offset)  // Aplica el offset
                .take(limit == -1 ? Long.MAX_VALUE : limit)  // Aplica el limit
                .map(challenge -> {
                    // Convierte el Challenge a ChallengeDto
                    ChallengeDto challengeDto = challengeConverter.convertDocumentToDto(challenge, ChallengeDto.class);

                    GenericResultDto<ChallengeDto> resultDto = new GenericResultDto<>();
                    resultDto.setInfo(offset, limit, 1, new ChallengeDto[]{challengeDto});
                    return resultDto;
                });
    }

    @Cacheable(value = "challenges", key = "{#offset, #limit}", unless = "#result==null")
    @Override
    public Mono<GenericResultDto<ChallengeDto>> getAllChallenges(int offset, int limit) {

        Mono<Long> countMono = challengeRepository.count();
        Flux<ChallengeDto> challengeDtoFlux = challengeConverter.convertDocumentFluxToDtoFlux(
                challengeRepository.findAllByUuidNotNullExcludingTestingValues()
                        .skip(offset)
                        .take(limit),
                ChallengeDto.class);

        return countMono.zipWith(challengeDtoFlux.collectList(), (totalCount, challenges) -> {
            ChallengeDto[] challengeArray = challenges.toArray(new ChallengeDto[0]);
            return new GenericResultDto<>(offset, limit, totalCount.intValue(), challengeArray);
        }).onErrorResume(e -> Mono.just(new GenericResultDto<>(offset, limit, 0, new ChallengeDto[0])));

    }

    @Cacheable(value = "solutions", key = "{#idChallenge, #idLanguage}", unless = "#result==null")
    @Override
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
                            )
                            .collectList()
                            .flatMap(solutions ->
                                    solutionConverter.convertDocumentFluxToDtoFlux(Flux.fromIterable(solutions), SolutionDto.class)
                                            .collectList()
                            )
                            .map(solutionDtos -> {
                                GenericResultDto<SolutionDto> resultDto = new GenericResultDto<>();
                                resultDto.setInfo(0, solutionDtos.size(), solutionDtos.size(), solutionDtos.toArray(new SolutionDto[0]));
                                return resultDto;
                            });
                });
    }

    @CacheEvict(value = {"challenges", "solutions"}, allEntries = true)
    @Override
    public Mono<SolutionDto> addSolution(SolutionDto solutionDto) {

        Mono<UUID> challengeIdMono = validateUUID(String.valueOf(solutionDto.getIdChallenge()));
        Mono<UUID> languageIdMono = validateUUID(String.valueOf(solutionDto.getIdLanguage()));

        return Mono.zip(challengeIdMono, languageIdMono)
                .flatMap(tuple -> {
                    UUID challengeId = tuple.getT1();
                    UUID languageId = tuple.getT2();


                    return ILanguageService.findByIdLanguage(languageId)
                            .switchIfEmpty(Mono.error(new LanguageNotFoundException(String.format(LANGUAGE_NOT_FOUND_ERROR, languageId))))
                            .flatMap(language -> challengeRepository.findByUuid(challengeId))
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
    public Mono<String> updateResourceByUuid(String id, Map<String, Object> updates) {
        return validateUUID(id)
                .flatMap(resourceId -> challengeRepository.findByUuid(resourceId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Resource with id " + resourceId + NOT_FOUND)))
                        .flatMap(resource -> {
                            updates.forEach((key, value) -> {
                                Field field = ReflectionUtils.findField(resource.getClass(), key);
                                if (field != null) {
                                    ReflectionUtils.setField(field, resource, value);
                                }
                            });
                            return challengeRepository.save(resource);
                        })
                        .then(Mono.just("Resource updated successfully"))
                )
                .doOnSuccess(resultDto -> log.info("Resource updated with ID: {}", id))
                .doOnError(error -> log.error("Error occurred while updating resource: {}", error.getMessage()));
    }

    @Override
    public Mono<ChallengeDto> addChallenge(ChallengeCreateDto challengeCreateDto) {
        String codingLanguage = challengeCreateDto.getLanguage();

        Topic topic;
        try {
            topic = Topic.fromDisplayName(String.valueOf(challengeCreateDto.getTopic()));
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException("Invalid topic provided: " + challengeCreateDto.getTopic()));
        }

        return ILanguageService.findFirstByLanguageName(codingLanguage)
                .switchIfEmpty(Mono.error(new LanguageNotFoundException("Language " + codingLanguage + " is not valid")))
                .flatMap(existingLanguage -> {
                    SolutionDocument solution = SolutionDocument.builder()
                            .uuid(UUID.randomUUID())
                            .solutionText(challengeCreateDto.getSolution())
                            .idLanguage(existingLanguage.getIdLanguage())
                            .build();
                    return solutionRepository.save(solution)
                            .flatMap(savedSolution -> {
                                ChallengeDocument challenge = buildChallengeDocument(
                                        challengeCreateDto,
                                        existingLanguage,
                                        savedSolution.getUuid(),
                                        topic,
                                        challengeCreateDto.getTags());
                                return challengeRepository.save(challenge)
                                        .map(savedChallenge -> challengeConverter.convertDocumentToDto(challenge,
                                                ChallengeDto.class));
                            });
                });
    }

    private ChallengeDocument buildChallengeDocument(ChallengeCreateDto dto, LanguageDocument language, UUID solutionId, Topic topic, List<UUID> tags) {
        DetailDocument detail = new DetailDocument(dto.getDescription());

        return ChallengeDocument.builder()
                .uuid(UUID.randomUUID())
                .title(dto.getChallengeTitle())
                .level(dto.getLevel().toString())
                .detail(detail)
                .languages(Set.of(language))
                .solutions(List.of(solutionId))
                .topic(topic)
                .tags(tags)
                .build();
    }


    private Mono<UUID> validateUUID(String id) {
        boolean validUUID = !StringUtils.isEmpty(id) && UUID_FORM.matcher(id).matches();

        if (!validUUID) {
            log.warn("Invalid ID format.");
            return Mono.error(new BadUUIDException("Invalid ID format. Please indicate the correct format."));
        }

        return Mono.just(UUID.fromString(id));
    }

    public Mono<DeleteResponseDto> deleteChallengeById(String id) {
        return validateUUID(id)
                .flatMap(challengeId -> challengeRepository.deleteByUuid(challengeId)
                        .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, id))))
                        .thenReturn(new DeleteResponseDto(id, "Challenge deleted successfully."))
                )
                .doOnSuccess(response -> log.info("Challenge deleted with ID: {}", response.getId()))
                .doOnError(error -> log.error("Error occurred while deleting challenge: {}", error.getMessage()));
    }

    @Override
    public Mono<ChallengeListDto> getChallengesByTopic(Topic topic, int page, int size) {
        Logger log = LoggerFactory.getLogger(getClass());
        challengeRepository.findByTopic(topic)
                .count()
                .doOnSuccess(count -> log.info("All challenges found: {}", count))                .subscribe();
        if (topic == null) {
            return Mono.just(ChallengeListDto.builder()
                    .results(new ArrayList<>())
                    .total(0)
                    .build());
        }

        Flux<ChallengeDocument> challengesFlux = challengeRepository.findByTopic(topic);

        if (challengesFlux == null) {
            return Mono.just(ChallengeListDto.builder()
                    .results(new ArrayList<>())
                    .total(0)
                    .build());
        }

        return challengeRepository.findByTopic(topic)
                .doOnNext(challenge -> log.info("Challenge found: {}", challenge))
                .collectList()
                .doOnSuccess(challenges -> log.info("All found: {}", challenges.size()))
                .defaultIfEmpty(new ArrayList<>())
                .map(challenges -> {
                    List<ChallengeDto> challengeDtos = challenges.stream()
                            .map(challenge -> challengeConverter.convertDocumentToDto(challenge, ChallengeDto.class))
                            .toList();

                    return ChallengeListDto.builder()
                            .results(challengeDtos)
                            .total(challengeDtos.size())
                            .build();
                })
                .switchIfEmpty(Mono.just(ChallengeListDto.builder()
                        .results(new ArrayList<>())
                        .total(0)
                        .build()));

    }

    @Override
    public Mono<FavoriteDto> addChallengeToFavorites(String challengeId, String userId) {

        Mono<UUID> challengeIdMono = validateUUID(String.valueOf(challengeId));
        Mono<UUID> userIdMono = validateUUID(String.valueOf(userId));

        return Mono.zip(challengeIdMono, userIdMono)
                .flatMap(Uuidtuple -> {
                    UUID challengeUuid = Uuidtuple.getT1();
                    UUID userUuid = Uuidtuple.getT2();

                    return challengeRepository.findByUuid(challengeUuid)
                            .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeUuid))))
                            .flatMap(challenge -> userService.addChallengeToFavorites(userUuid.toString(), challengeUuid.toString())
                                    .onErrorResume(throwable -> Mono.error(new InternalServerErrorException(throwable.getMessage())))
                                    .flatMap(isAddedToUsersFavorites -> {
                                        if (Boolean.TRUE.equals(isAddedToUsersFavorites) ||
                                                Optional.ofNullable(challenge.getTimesFavorite()).orElse(0) == 0) {
                                            challenge.increaseTimesFavorite();
                                            return challengeRepository.save(challenge);
                                        }
                                        return Mono.just(challenge);
                                    })
                                    .map(savedChallenge -> new FavoriteDto(true, savedChallenge.getTimesFavorite())));
                });
    }

    @Override
    public Mono<FavoriteDto> removeChallengeFromFavorites(String challengeId, String userId) {
        Mono<UUID> challengeIdMono = validateUUID(String.valueOf(challengeId));
        Mono<UUID> languageIdMono = validateUUID(String.valueOf(userId));

        return Mono.zip(challengeIdMono, languageIdMono)
                .flatMap(Uuidtuple -> {
                    UUID challengeUuid = Uuidtuple.getT1();
                    UUID userUuid = Uuidtuple.getT2();

                    return challengeRepository.findByUuid(challengeUuid)
                            .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeUuid))))
                            .flatMap(challenge -> userService.removeChallengeFromFavorites(userUuid.toString(), challengeUuid.toString())
                                    .onErrorResume(throwable -> Mono.error(new InternalServerErrorException(throwable.getMessage())))
                                    .flatMap(isRemovedFromUsersFavorites -> {
                                        if (Boolean.TRUE.equals(isRemovedFromUsersFavorites) ||
                                                Optional.ofNullable(challenge.getTimesFavorite()).orElse(0) == 0) {
                                            challenge.decreaseTimesFavorite();
                                            return challengeRepository.save(challenge);
                                        }
                                        return Mono.just(challenge);
                                    })
                                    .map(savedChallenge -> new FavoriteDto(false, savedChallenge.getTimesFavorite())));
                });
    }

    @Override
    public Mono<ChallengeDto> updateChallenge(String challengeId, ChallengeCreateDto challengeCreateDto) {
        validateUUID(String.valueOf(challengeId));
        String codingLanguage = challengeCreateDto.getLanguage();
        return ILanguageService.findFirstByLanguageName(codingLanguage)
                .switchIfEmpty(Mono.error(new LanguageNotFoundException("Language " + codingLanguage + " is not valid")))
                .flatMap(newLanguage -> validateUUID(String.valueOf(challengeId))
                        .flatMap(validId -> challengeRepository.findByUuid(validId)
                                .switchIfEmpty(Mono.error(new ChallengeNotFoundException(
                                        String.format(CHALLENGE_NOT_FOUND_ERROR, validId))))
                                .flatMap(challengeDocument -> {
                                    log.info("Challenge found for challengeId: {}", validId);

                                    return tagService.getValidatedTags(challengeCreateDto.getTags())
                                            .flatMap(allTagsValid -> {
                                                if (!allTagsValid) {
                                                    return Mono.error(new TagNotFoundException("One or more tags are invalid"));
                                                }

                                                SolutionDocument solutionDocument = buildSolutionDocument(newLanguage, challengeCreateDto);
                                                return solutionRepository.save(solutionDocument)
                                                        .flatMap(savedSolution -> {
                                                            log.info("New solution successfully saved for challengeId: {}", validId);
                                                            ChallengeDocument newChallengeDocument = updateChallengeDocument(
                                                                    challengeDocument, challengeCreateDto, newLanguage, solutionDocument.getUuid());

                                                            return challengeRepository.save(newChallengeDocument)
                                                                    .map(savedChallenge -> {
                                                                        log.info("Challenge {} successfully updated in database.", validId);
                                                                        log.debug("Saved challenge tags: {}", savedChallenge.getTags());
                                                                        return challengeConverter.convertDocumentToDto(savedChallenge,
                                                                                ChallengeDto.class);
                                                                    });
                                                        });
                                            });
                                }))
                );
    }

    @Override
    public Mono<BookmarkDto> addChallengeToBookmarks(String challengeId, String userId) {

        Mono<UUID> challengeIdMono = validateUUID(String.valueOf(challengeId));
        Mono<UUID> userIdMono = validateUUID(String.valueOf(userId));

        return Mono.zip(challengeIdMono, userIdMono)
                .flatMap(Uuidtuple -> {
                    UUID challengeUuid = Uuidtuple.getT1();
                    UUID userUuid = Uuidtuple.getT2();

                    return challengeRepository.findByUuid(challengeUuid)
                            .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeUuid))))
                            .flatMap(challenge -> userService.addChallengeToBookmarks(userUuid.toString(), challengeUuid.toString())
                                    .onErrorResume(throwable -> Mono.error(new InternalServerErrorException(throwable.getMessage())))
                                    .flatMap(isAddedToUsersBookmarks -> {
                                        if (Boolean.TRUE.equals(isAddedToUsersBookmarks) ||
                                        Optional.ofNullable(challenge.getTimesBookmark()).orElse(0) == 0) {
                                            challenge.increaseTimesBookmark();
                                            return challengeRepository.save(challenge);
                                        }
                                        return Mono.just(challenge);
                                    })
                                    .map(savedChallenge -> new BookmarkDto( true, savedChallenge.getTimesBookmark())));
                });
    }

    @Override
    public Mono<BookmarkDto> removeChallengeFromBookmarks(String challengeId, String userId) {
        Mono<UUID> challengeIdMono = validateUUID(String.valueOf(challengeId));
        Mono<UUID> languageIdMono = validateUUID(String.valueOf(userId));

        return Mono.zip(challengeIdMono, languageIdMono)
                .flatMap(Uuidtuple -> {
                    UUID challengeUuid = Uuidtuple.getT1();
                    UUID userUuid = Uuidtuple.getT2();

                    return challengeRepository.findByUuid(challengeUuid)
                            .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeUuid))))
                            .flatMap(challenge -> userService.removeChallengeFromBookmarks(userUuid.toString(), challengeUuid.toString())
                                    .onErrorResume(throwable -> Mono.error(new InternalServerErrorException(throwable.getMessage())))
                                    .flatMap(isRemovedFromUsersBookmarks -> {
                                        if (Boolean.TRUE.equals(isRemovedFromUsersBookmarks) ||
                                                Optional.ofNullable(challenge.getTimesBookmark()).orElse(0) == 0) {
                                            challenge.decreaseTimesBookmark();
                                            return challengeRepository.save(challenge);
                                        }
                                        return Mono.just(challenge);
                                    })
                                    .map(savedChallenge -> new BookmarkDto(false, savedChallenge.getTimesBookmark())));
                });
    }

    @Override
    public Mono<SolvedDto> addChallengeToSolved(String challengeId) {
                
        Mono<UUID> challengeIdMono = validateUUID(challengeId);

        return challengeIdMono
             .flatMap(challengeUuid -> {
                        return challengeRepository.findByUuid(challengeUuid)
                                .switchIfEmpty(Mono.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeUuid))))
                                        .flatMap(challenge -> {
                                                challenge.increaseTimesSolved();
                                                return challengeRepository.save(challenge);
                                        })
                                        .map(updatedChallenge -> new SolvedDto(true, updatedChallenge.getTimesSolved()));
                        });
        }


    private SolutionDocument buildSolutionDocument(LanguageDocument language, ChallengeCreateDto challengeCreateDto){
        return SolutionDocument.builder()
                .uuid(UUID.randomUUID())
                .idLanguage(language.getIdLanguage())
                .solutionText(challengeCreateDto.getSolution())
                .build();
    }

    private static ChallengeDocument updateChallengeDocument (ChallengeDocument currentChallenge, ChallengeCreateDto dto, LanguageDocument language, UUID solutionId){
        currentChallenge.setTitle(dto.getChallengeTitle());
        currentChallenge.setLevel(String.valueOf(dto.getLevel()));
        currentChallenge.setDetail(new DetailDocument(dto.getDescription()));
        currentChallenge.setLanguages(Set.of(language));
        currentChallenge.setSolutions(List.of(solutionId));
        currentChallenge.setTopic(dto.getTopic());
        currentChallenge.setTags(dto.getTags());

        return currentChallenge;
    }
}