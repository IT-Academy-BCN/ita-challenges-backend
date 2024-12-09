package com.itachallenge.user.service;

import com.itachallenge.user.document.SolutionDocument;
import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.*;
import com.itachallenge.user.enums.ChallengeStatus;
import com.itachallenge.user.exception.ChallengeNotFoundException;
import com.itachallenge.user.exception.UnmodifiableSolutionException;
import com.itachallenge.user.helper.BuildUserStatisticsDto;
import com.itachallenge.user.helper.ConverterDocumentToDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.apache.commons.lang3.function.TriFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class UserSolutionServiceImp implements IUserSolutionService {

    private static final Logger log = LoggerFactory.getLogger(UserSolutionServiceImp.class);
    private final IUserSolutionRepository userSolutionRepository;
    private final ConverterDocumentToDto converter;
    private final BuildUserStatisticsDto buildUserStatisticsDto;
    SecureRandom random = new SecureRandom();
    private static final String CHALLENGE_NOT_FOUND_ERROR = "Challenge with id %s not found";

    public UserSolutionServiceImp(IUserSolutionRepository userSolutionRepository, ConverterDocumentToDto converter, BuildUserStatisticsDto buildUserStatisticsDto) {
        this.userSolutionRepository = userSolutionRepository;
        this.converter = converter;
        this.buildUserStatisticsDto = buildUserStatisticsDto;
    }

    public Mono<SolutionUserDto<UserScoreDto>> getChallengeById(String idUser, String idChallenge, String idLanguage) {
        UUID userUuid = UUID.fromString(idUser);
        UUID challengeUuid = UUID.fromString(idChallenge);
        UUID languageUuid = UUID.fromString(idLanguage);

        return this.userSolutionRepository.findByUserId(userUuid)
                .filter(userScore -> userScore.getChallengeId().equals(challengeUuid) && userScore.getLanguageId().equals(languageUuid))
                .flatMap(userScore -> converter.fromUserScoreDocumentToUserScoreDto(Flux.just(userScore)))
                .collectList()
                .map(userScoreDtos -> {
                    SolutionUserDto<UserScoreDto> solutionUserDto = new SolutionUserDto<>();
                    int count = userScoreDtos.size();
                    solutionUserDto.setInfo(0, 1, count, userScoreDtos.toArray(new UserScoreDto[0]));
                    return solutionUserDto;
                });
    }

    @Override
    public Mono<UserSolutionScoreDto> addSolution(UserSolutionDto userSolutionDto) {
        UUID challengeUuid = UUID.fromString(userSolutionDto.getChallengeId());
        UUID languageUuid = UUID.fromString(userSolutionDto.getLanguageId());
        UUID userUuid = UUID.fromString(userSolutionDto.getUserId());
        String status = userSolutionDto.getStatus();
        ChallengeStatus challengeStatus;
        List<SolutionDocument> solutionDocuments;

        solutionDocuments = List.of(
                SolutionDocument.builder()
                        .uuid(UUID.randomUUID())
                        .solutionText(userSolutionDto.getSolutionText())
                        .build()
        );
        challengeStatus = determineChallengeStatus(status);

        if (challengeStatus == null) {
            log.error("POST operation failed due to invalid challenge status parameter");
            return Mono.error(new IllegalArgumentException("Status not allowed"));
        }
        return saveValidSolution(userUuid, challengeUuid, languageUuid, challengeStatus, solutionDocuments)
                .map(savedDocument -> UserSolutionScoreDto.builder()
                        .userId(String.valueOf(savedDocument.getUserId()))
                        .languageId(String.valueOf(savedDocument.getLanguageId()))
                        .challengeId(String.valueOf(savedDocument.getChallengeId()))
                        .solutionText(savedDocument.getSolutionDocument().get(0).getSolutionText())
                        .score(savedDocument.getScore())
                        .build())
                .doOnSuccess(userSolutionDocument -> log.info("Successfully POSTed solution"))
                .doOnError(error -> log.error("POST operation failed with error message: {}", error.getMessage()));
    }

    public Mono<UserSolutionDocument> markAsBookmarked(String uuidChallenge, String uuidLanguage, String uuidUser, boolean bookmarked) {
        UUID challengeId = UUID.fromString(uuidChallenge);
        UUID languageId = UUID.fromString(uuidLanguage);
        UUID userId = UUID.fromString(uuidUser);

        return userSolutionRepository
                .findByUserIdAndChallengeIdAndLanguageId(userId, challengeId, languageId)
                .flatMap(userSolutionDocument -> {
                    userSolutionDocument.setBookmarked(bookmarked);
                    return userSolutionRepository.save(userSolutionDocument).thenReturn(userSolutionDocument);
                })
                .switchIfEmpty(createAndSaveNewBookmark(challengeId, languageId, userId, bookmarked));
    }

    private Mono<UserSolutionDocument> createAndSaveNewBookmark(UUID challengeId, UUID languageId, UUID userId, boolean bookmarked) {
        UserSolutionDocument newDocument = UserSolutionDocument.builder()
                .uuid(UUID.randomUUID())
                .userId(userId)
                .challengeId(challengeId)
                .languageId(languageId)
                .bookmarked(bookmarked)
                .build();

        return userSolutionRepository.save(newDocument).thenReturn(newDocument);
    }

    private Mono<UserSolutionDocument> saveValidSolution(UUID userUuid, UUID challengeUuid, UUID languageUuid, ChallengeStatus challengeStatus, List<SolutionDocument> solutionDocuments) {
        return userSolutionRepository.findByUserIdAndChallengeIdAndLanguageId(userUuid, challengeUuid, languageUuid)
                .flatMap(existingSolution -> {
                    if (existingSolution.getStatus().equals(ChallengeStatus.ENDED)) {
                        return Mono.error(new UnmodifiableSolutionException("Existing solution has status ENDED"));
                    }
                    existingSolution.setSolutionDocument(solutionDocuments);
                    existingSolution.setStatus(challengeStatus);
                    return userSolutionRepository.save(existingSolution);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    UserSolutionDocument userSolutionDocument = UserSolutionDocument.builder()
                            .uuid(UUID.randomUUID())
                            .userId(userUuid)
                            .challengeId(challengeUuid)
                            .languageId(languageUuid)
                            .status(challengeStatus)
                            .score(13)    // TODO GET SCORE FROM SCORE SERVICE
                            .solutionDocument(solutionDocuments)
                            .build();
                    return userSolutionRepository.save(userSolutionDocument);
                }));
    }

    private ChallengeStatus determineChallengeStatus(String status) {

        ChallengeStatus challengeStatus = null;

        if(status == null || status.isEmpty()) {
            challengeStatus = ChallengeStatus.STARTED;
        } else if (status.equalsIgnoreCase(ChallengeStatus.ENDED.getValue())) {
            challengeStatus = ChallengeStatus.ENDED;
    }
        return challengeStatus;
    }

    public Flux<UserSolutionDto> showAllUserSolutions(UUID userUuid) {
        return userSolutionRepository.findByUserId(userUuid)
                .flatMap(converter::fromUserSolutionDocumentToUserSolutionDto);
    }

    @Override
    public Mono<List<ChallengeStatisticsDto>> getChallengeStatistics(List<UUID> challengeIds) {

        List<ChallengeStatisticsDto> challengesList = new ArrayList<>();

        try{
            for (UUID id : challengeIds) {
                //TODO: missing check if UUID is correctly constructed.

                //TODO: missing call repository for get statistics of challenge.

                //TODO: delete below code when uppers todo are implemented.
                challengesList.add(new ChallengeStatisticsDto(id, random.nextInt(1000), random.nextFloat (100)));
            }
        }catch(Exception ex){
            //TODO: missing error control
        }
        return Mono.just(challengesList);
    }

    @Override
    public Mono<Long> getBookmarkCountByIdChallenge(UUID idChallenge) {
        return userSolutionRepository.countByChallengeIdAndBookmarked(idChallenge, true);
    }

    @Override
    public Mono<Float> getChallengeUsersPercentage(UUID idChallenge) {

        Mono<Long> startedChallengesCount = userSolutionRepository.findByChallengeIdAndStatus(idChallenge, ChallengeStatus.STARTED).count();

        Mono<Long> endedChallengesCount = userSolutionRepository.findByChallengeIdAndStatus(idChallenge, ChallengeStatus.ENDED).count();

        Mono<Long> allChallenges = userSolutionRepository.findByChallengeId(idChallenge).count();

        Mono<Float> percentage = startedChallengesCount.zipWith(endedChallengesCount, (value1, value2) -> value1 + value2)
                .flatMap(sum -> allChallenges.flatMap(value3 -> {
                    if (value3 == 0) {
                        return Mono.error(new ChallengeNotFoundException("Challenge's id " + idChallenge + " is not found"));
                    }
                    return Mono.just((sum * 100f) / value3);
                }));

        return percentage;
    }

    private Flux<UserSolutionDocument> getUserSolutions() {
        return userSolutionRepository.findAll();
    }

    private Flux<UserSolutionDocument> getUserSolutionsChallenge(List<UserSolutionDocument> userSolutions, UUID challengeId) {
        List<UserSolutionDocument> userSolutionsChallenge = userSolutions.stream()
                .filter(us -> challengeId.equals(us.getChallengeId()))
                .toList();

        if (userSolutionsChallenge.isEmpty()) {
            return Flux.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeId)));
        }

        return Flux.fromIterable(userSolutionsChallenge);
    }

    @Override
    public Mono<UsersTotalStatisticsDto> getUserTotalStatistics(String idUser, String idLanguage) {

        UUID userUuid = UUID.fromString(idUser);
        UUID languageUuid = UUID.fromString(idLanguage);

        // A centralized method ("countChallengesWithErrorHandling()") is used to handle errors and perform the queries.
        Mono<Long> completedChallengesMono = countChallengesWithErrorHandling(
                userSolutionRepository::countChallengesByStatusAndLanguage,
                userUuid, languageUuid, Arrays.asList(ChallengeStatus.ENDED, ChallengeStatus.SENT, ChallengeStatus.SCORE_PENDING));

        Mono<Long> savedChallenesMono = countChallengesWithErrorHandling(
                userSolutionRepository::countChallengesByStatusAndLanguage,
                userUuid, languageUuid, List.of(ChallengeStatus.STARTED));

        Mono<Long> scorePendingChallengesMono = countChallengesWithErrorHandling(
                userSolutionRepository::countChallengesByStatusAndLanguage,
                userUuid, languageUuid,  Arrays.asList(ChallengeStatus.SENT, ChallengeStatus.SCORE_PENDING));

        Mono<Long> passedChallengesMono = countChallengesWithErrorHandling(
                userSolutionRepository::countByChallengeStatusAndLanguageAndScoreAmount,
                userUuid, languageUuid, List.of(ChallengeStatus.ENDED));

        return Mono.zip(completedChallengesMono, savedChallenesMono, scorePendingChallengesMono, passedChallengesMono)
                .map(tuple ->
                        buildUserStatisticsDto.fromUserTotalStatisticsToDto(userUuid, languageUuid,
                        tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4())
                )
                // General exception handling in case something fails in the queries.
                .onErrorResume(DataAccessResourceFailureException.class, e -> {
                    log.error("Database access failure while fetching solutions for user: {}", userUuid, e);
                    return Mono.error(new DataAccessResourceFailureException("Error accessing the database for user: " + userUuid, e));
                })
                .onErrorResume(RuntimeException.class, e -> {
                    log.error("Runtime error while fetching user solutions for user {}: {}", userUuid, e.getMessage());
                    return Mono.error(new RuntimeException("Runtime error while fetching user solutions for user: " + userUuid, e));
                })
                .onErrorResume(Exception.class, e -> {
                    log.error("Unexpected error while fetching solutions for user: {}", userUuid, e);
                    return Mono.error(new Exception("Unexpected error while fetching solutions for user: " + userUuid, e));
                });
    }

    // Method that centralizes repository queries with error handling.
    /**
     *  The TriFunction is used to create a single method that handles all database queries.
     *  (One of the TriFunction´arguments is the repository method itself)
     */
    protected Mono<Long> countChallengesWithErrorHandling(
        TriFunction<UUID, UUID, List<ChallengeStatus>, Mono<Long>> repositoryMethod,
        UUID userUuid,
        UUID languageUuid,
        List<ChallengeStatus> statuses) {

        final long ERROR_VALUE = -1L;

        if (statuses.isEmpty()) { return Mono.just(ERROR_VALUE);}
        // Call the provided repository method, passing the appropriate parameters.
        return repositoryMethod.apply(userUuid, languageUuid, statuses)
                .onErrorReturn(ERROR_VALUE); // If an error occurs, returns -1L.
    }
}

