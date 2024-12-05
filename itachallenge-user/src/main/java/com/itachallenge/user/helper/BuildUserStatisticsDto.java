package com.itachallenge.user.helper;

import com.itachallenge.user.dtos.UsersTotalStatisticsDto;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class BuildUserStatisticsDto {
    private static final long ERROR_VALUE = -1L;

    public UsersTotalStatisticsDto fromUserTotalStatisticsToDto (UUID userUuid, UUID languageUuid, long saved,
                                                                 long completed, long scorePending, long passed ) {

        // Assign the values to the internal class (ChallengesStatistics) of the DTO
        UsersTotalStatisticsDto.ChallengesStatistics challengesStatistics = new UsersTotalStatisticsDto.ChallengesStatistics();
        challengesStatistics.setSaved(saved); // saved
        challengesStatistics.setCompleted(completed); // completed
        challengesStatistics.setScorePending(scorePending); // scorePending
        challengesStatistics.setPassed(passed); // passed

        // Check if there was any error in the queries (indicating that -1L was returned)
        boolean errorOccurred = saved == ERROR_VALUE || completed == ERROR_VALUE || scorePending == ERROR_VALUE ||
                passed == ERROR_VALUE;

        return UsersTotalStatisticsDto.builder()
                .userUuid(userUuid)
                .languageUuid(languageUuid)
                .challengesStatistics(challengesStatistics)
                .errorOccurred(errorOccurred)
                .build();
    }

}
