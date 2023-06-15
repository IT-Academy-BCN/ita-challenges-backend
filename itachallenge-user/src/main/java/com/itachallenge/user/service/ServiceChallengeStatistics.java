package com.itachallenge.user.service;

import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServiceChallengeStatistics implements IServiceChallengeStatistics {
    //region ATTRIBUTES


    //endregion ATTRIBUTES


    //region CONSTRUCTOR

    //endregion CONSTRUCTOR


    //region METHODS

    @Override
    public Mono<List<ChallengeStatisticsDto>> GetChallengeStatistics(List<UUID> challengeIds) {
        //region VARIABLES
        List<ChallengeStatisticsDto> challengesList = new ArrayList<>();

        //endregion VARIABLES


        //region ACTIONS
        try{
            for (UUID id : challengeIds) {
                //todo: missing check if UUID is correctly constructed.

                //todo: missing call repository for get challenge statistics.

                //todo: below code delete when uppers todo are implemented.
                challengesList.add(new ChallengeStatisticsDto(id,(int)(Math.random()*1000), (float)(Math.random()*100)));

            }
        }catch(Exception ex){
            //todo: missing error control
        }

        //endregion ACTIONS


        // OUT
        return Mono.just(challengesList);

    }

    //endregion METHODS


}
