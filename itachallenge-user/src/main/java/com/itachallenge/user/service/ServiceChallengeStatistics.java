package com.itachallenge.user.service;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.itachallenge.user.validators.UUIDValidator.isValidUUID;

@Service
public class ServiceChallengeStatistics implements IServiceChallengeStatistics {

    //@Autowired
    private final IUserSolutionRepository userSolutionRepository;

    //SecureRandom random = new SecureRandom();

    public ServiceChallengeStatistics(IUserSolutionRepository userSolutionRepository) {
        this.userSolutionRepository = userSolutionRepository;
    }


    @Override
    public Mono<ChallengeStatisticsDto> getChallengeStatistics(UUID challengeId) {

        if (!isValidUUID(String.valueOf(challengeId))) {
            return Mono.error(new IllegalArgumentException("Invalid UUID for challengeId"));
        }

        Mono<Integer> getPopularity = getChallengePopularity(challengeId);
        Mono<Float> getPercentage = getChallengeUsersPercentage(challengeId);

        return Mono.zip(getPopularity, getPercentage)
                .map(tuple -> {
                    Integer popularity = tuple.getT1();
                    Float percentage = tuple.getT2();
                    return new ChallengeStatisticsDto(challengeId, popularity, percentage);
                });
    }

    @Override
    public Mono<Float> getChallengeUsersPercentage(UUID challengeId) {
        if (!isValidUUID(String.valueOf(challengeId))) {
            return Mono.error(new IllegalArgumentException("Invalid UUID for challengeId"));
        }
        return getUserSolutions()
                .map(userSolutions -> {
                    if (userSolutions == null || userSolutions.isEmpty()) {
                        //Mono.error(new NullException("No userSolution found corresponding to this challenge...."));
                        return 0.0f;
                    } else {
                        List<UserSolutionDocument> userSolutionsChallenge = getUserSolutionsChallenge(userSolutions, challengeId);
                        return calculatePercentage(userSolutionsChallenge.size(), userSolutions.size());
                    }

                });
    }

    private float calculatePercentage(int numerator, int denominator) {
        if (denominator == 0) {
            return 0;
        }
        return ((float) numerator * 100 / denominator);
    }


    @Override
    public Mono<Integer> getChallengePopularity(UUID challengeId) {
        if (!isValidUUID(String.valueOf(challengeId))) {
            return Mono.error(new IllegalArgumentException("Invalid UUID for challengeId"));
        }
        return getUserSolutions()
                .map(userSolutions ->
                        (int) userSolutions.stream()
                                .filter(userSolution -> isBookmarkedChallenge(userSolution, challengeId))
                                .count()
                );
    }

    private boolean isBookmarkedChallenge(UserSolutionDocument userSolution, UUID challengeId) {
        return challengeId.equals(userSolution.getChallengeId()) && userSolution.isBookmarked();
    }

    public Mono<List<UUID>> getGlobalChallengesIds() {
        return userSolutionRepository.findAll()
                .map(UserSolutionDocument::getChallengeId)
                .collectList();
    }

    Mono<List<UserSolutionDocument>> getUserSolutions() {
        return userSolutionRepository.findAll().collectList();
    }

    List<UserSolutionDocument> getUserSolutionsChallenge(List<UserSolutionDocument> userSolutions, UUID challengeId) {
        if (userSolutions == null || challengeId == null) {
            // Handle null input gracefully, throw an exception, or return an empty list based on your requirements
            return Collections.emptyList();
        }

        return userSolutions.stream()
                .filter(us -> Objects.equals(challengeId, us.getChallengeId()))
                .toList();
    }
}
