package com.itachallenge.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UsersTotalStatisticsDto {

    private UUID userUuid;
    private UUID languageUuid;
    private ChallengesStatistics challengesStatistics;
    private boolean errorOccurred; // To indicate an Error has occurred

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengesStatistics {
        private long completed; // Desafíos completados (ENDED, SENT, SCORE_PENDING)
        private long saved; // Desafíos guardados (STARTED)
        private long scorePending; // Desafíos con puntuación pendiente (SENT, SCORE_PENDING)
        private long passed; // Desafíos aprobados (ENDED and score >= 75)
    }

}
