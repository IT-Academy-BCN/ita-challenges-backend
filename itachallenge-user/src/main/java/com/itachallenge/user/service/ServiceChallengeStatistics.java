package com.itachallenge.user.service;

import com.itachallenge.user.dtos.ChallengeStatisticsDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class ServiceChallengeStatistics implements IServiceChallengeStatistics {
    //region ATTRIBUTES
    Random randrom = new Random();

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
                //TODO: missing check if UUID is correctly constructed.

                //TODO: missing call repository for get statistics of challenge.

                //TODO: delete below code when uppers todo are implemented.
                challengesList.add(new ChallengeStatisticsDto(id, randrom.nextInt(1000), randrom.nextFloat (100)));

            }
        }catch(Exception ex){
            //TODO: missing error control
        }

        //endregion ACTIONS


        // OUT
        return Mono.just(challengesList);

    }

    //endregion METHODS


}
