package com.itachallenge.user.service;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import com.itachallenge.user.dtos.UserSolutionDto;
import com.itachallenge.user.enums.ChallengeStatus;
import com.itachallenge.user.repository.IUserSolutionRepository;
import com.itachallenge.user.exception.ChallengeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceChallengeStatistics implements IServiceChallengeStatistics {
    @Autowired
    private IUserSolutionRepository userSolutionRepository;
    SecureRandom random = new SecureRandom();
    private static final String CHALLENGE_NOT_FOUND_ERROR = "Challenge with id %s not found";

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
                .collect(Collectors.toList());

        if (userSolutionsChallenge.isEmpty()) {
            return Flux.error(new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeId)));
        }

        return Flux.fromIterable(userSolutionsChallenge);
    }
}
