package com.itachallenge.user.service;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.ChallengeStatisticsDto;
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
    //region ATTRIBUTES
    SecureRandom random = new SecureRandom();

    private static final String CHALLENGE_NOT_FOUND_ERROR = "Challenge with id %s not found";

    //endregion ATTRIBUTES


    //region METHODS
    @Override
    public Mono<List<ChallengeStatisticsDto>> getChallengeStatistics(List<UUID> challengeIds) {
        //region VARIABLES
        List<ChallengeStatisticsDto> challengesList = new ArrayList<>();

        //endregion VARIABLES


        //region ACTIONS
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

        //endregion ACTIONS


        // OUT
        return Mono.just(challengesList);

    }

    @Override
    public Mono<Long> getBookmarkCountByIdChallenge(UUID idChallenge) {
        return userSolutionRepository.countByChallengeIdAndBookmarked(idChallenge, true);
    }

    //endregion METHODS
    @Override
    public Mono<Float> getChallengeUsersPercentage(UUID idChallenge) {
        return getUserSolutions()
                .flatMapMany(Flux::fromIterable)
                .collectList()
                .flatMap(userSolutions -> {
                    if (userSolutions.isEmpty()) {
                        return Mono.just(0f);
                    }
                    List<UserSolutionDocument> userSolutionsChallenge = getUserSolutionsChallenge(userSolutions, idChallenge);
                    float percentage = ((float) userSolutionsChallenge.size() * 100 / userSolutions.size());
                    return Mono.just(percentage);
                });
    }

    private Mono<List<UserSolutionDocument>> getUserSolutions() {
        return userSolutionRepository
                .findAll()
                .collectList();
    }

    private List<UserSolutionDocument> getUserSolutionsChallenge(List<UserSolutionDocument> userSolutions, UUID challengeId) {
        List<UserSolutionDocument> userSolutionsChallenge = userSolutions.stream()
                .filter(us -> challengeId.equals(us.getChallengeId()))
                .collect(Collectors.toList());

        if (userSolutionsChallenge.isEmpty()) {
            throw new ChallengeNotFoundException(String.format(CHALLENGE_NOT_FOUND_ERROR, challengeId));
        }

        return userSolutionsChallenge;
    }
}
