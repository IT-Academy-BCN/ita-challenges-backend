package com.itachallenge.user.helper;

import ch.qos.logback.classic.Logger;
import com.itachallenge.user.dtos.UsersTotalStatisticsDto;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuple4;

import java.util.Arrays;
import java.util.UUID;

@Component
public class BuildUserStatisticsDto {
    private static final long ERROR_VALUE = -1L;
    private Logger log;

    public UsersTotalStatisticsDto fromUserTotalStatisticsToDto (UUID userUuid, UUID languageUuid, long saved,
                                                                 long completed, long scorePending, long passed ) {

        // Asignar los valores a la clase interna ( ChallengesStatistics ) del DTO
        UsersTotalStatisticsDto.ChallengesStatistics challengesStatistics = new UsersTotalStatisticsDto.ChallengesStatistics();
        challengesStatistics.setSaved(saved); // saved
        challengesStatistics.setCompleted(completed); // completed
        challengesStatistics.setScorePending(scorePending); // scorePending
        challengesStatistics.setPassed(passed); // passed

        // Verificar si hubo algún error en las consultas (indicando que se retornó -1L)
        boolean errorOccurred = saved == ERROR_VALUE || completed == ERROR_VALUE || scorePending == ERROR_VALUE ||
                passed == ERROR_VALUE;

        // Agregar logs para verificar los valores
        log.debug("Completed Challenges: {}", completed);
        log.debug("Saved Challenges: {}", saved);
        log.debug("Score Pending Challenges: {}", scorePending);
        log.debug("Passed Challenges: {}", passed);
        
        return UsersTotalStatisticsDto.builder()
                .userUuid(userUuid)
                .languageUuid(languageUuid)
                .challengesStatistics(challengesStatistics)
                .errorOccurred(errorOccurred)
                .build();
    }

}
