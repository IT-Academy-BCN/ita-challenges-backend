package com.itachallenge.user.helper;

import com.itachallenge.user.dtos.UsersTotalStatisticsDto;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;

import java.util.UUID;

@Component
public class BuildUserStatisticsDto {

    public UsersTotalStatisticsDto fromUserTotalStatisticsToDto (UUID userUuid, UUID languageUuid,
                                                                       Tuple4<Long, Long, Long, Long> tuple ) {

        // Crear el DTO con los valores obtenidos
        UsersTotalStatisticsDto totalStatisticsDto = new UsersTotalStatisticsDto();
        totalStatisticsDto.setUserUuid(userUuid);
        totalStatisticsDto.setLanguageUuid(languageUuid);

        // Asignar los valores a la clase interna ( ChallengesStatistics ) del DTO
        UsersTotalStatisticsDto.ChallengesStatistics challengesStatistics = new UsersTotalStatisticsDto.ChallengesStatistics();
        challengesStatistics.setSaved(tuple.getT1()); // saved
        challengesStatistics.setCompleted(tuple.getT2()); // completed
        challengesStatistics.setScorePending(tuple.getT3()); // socrePending
        challengesStatistics.setPassed(tuple.getT4()); // passed

        // Verificar si hubo algún error en las consultas (indicando que se retornó -1L)
        boolean errorOccurred = tuple.getT1() == -1L || tuple.getT2() == -1L || tuple.getT3() == -1L || tuple.getT4() == -1L;
        totalStatisticsDto.setErrorOccurred(errorOccurred);

        // Asignamos el objeto ChallengesStatistics al DTO principal
        totalStatisticsDto.setChallengesStatistics(challengesStatistics);

        return totalStatisticsDto;

    }

}
