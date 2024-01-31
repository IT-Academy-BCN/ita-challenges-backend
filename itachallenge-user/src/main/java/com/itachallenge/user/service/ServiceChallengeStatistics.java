package com.itachallenge.user.service;

import com.itachallenge.user.document.UserSolutionDocument;
import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import com.itachallenge.user.repository.IUserSolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ServiceChallengeStatistics implements IServiceChallengeStatistics {
    //region ATTRIBUTES
    SecureRandom random = new SecureRandom();
    @Autowired
    private IUserSolutionRepository userSolutionRepository;

    //endregion ATTRIBUTES


    //region METHODS
    @Override
    public Mono<List<ChallengeStatisticsDto>> getChallengeStatistics(List<UUID> challengeIds) {
        //region VARIABLES
        List<ChallengeStatisticsDto> challengesList = new ArrayList<>();

        //endregion VARIABLES


        //region ACTIONS
        try {
            for (UUID id : challengeIds) {
                //TODO: missing check if UUID is correctly constructed.

                //TODO: missing call repository for get statistics of challenge.

                //TODO: delete below code when uppers todo are implemented.
                challengesList.add(new ChallengeStatisticsDto(id, random.nextInt(1000), random.nextFloat(100)));

            }
        } catch (Exception ex) {
            //TODO: missing error control
        }

        //endregion ACTIONS


        // OUT
        return Mono.just(challengesList);

    }

    @Override
    public Mono<Float> getChallengeUsersPercentage(UUID challengeId) {

        float percentage;

        // List of all UserSolution with all status started and ended and empty
        List<UserSolutionDocument> userSolutionsV1 = getUserSolutions();

        // // List of all UserSolution of challenge with id challengeId
        List<UserSolutionDocument> userSolutionsChallenge = getUserSolutionsChallenge(userSolutionsV1, challengeId);

        percentage = ((float) userSolutionsChallenge.size()*100 / userSolutionsV1.size());
        return Mono.just(percentage);
    }

    List<UserSolutionDocument> getUserSolutions() {
        return userSolutionRepository
                .findAll()
                .collectList().block();
    }

    List<UserSolutionDocument> getUserSolutionsChallenge(List<UserSolutionDocument> userSolutions, UUID challengeId) {
        return userSolutions.stream()
                .filter(
                        us -> challengeId.equals(us.getChallengeId())
                ).toList();
    }
}
