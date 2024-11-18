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
    private String idLanguage;
    private ChallengesStatistics challengesStatistics;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChallengesStatistics {
        private long count; // Total de desafíos
        private long completed; // Desafíos completados (ENDED, SENT, SCORE_PENDING)
        private long saved; // Desafíos guardados (STARTED)
        private long scorePending; // Desafíos con puntuación pendiente (SENT, SCORE_PENDING)
        private long passed; // Desafíos aprobados (ENDED and score >= 75)
    }

}
