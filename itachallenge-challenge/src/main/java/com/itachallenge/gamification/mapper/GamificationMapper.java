package com.itachallenge.gamification.mapper;

import com.itachallenge.challenge.dto.gamification.PointHistoryEntryDto;
import com.itachallenge.gamification.document.UserScoreDocument;
import org.springframework.stereotype.Component;

@Component
public class GamificationMapper {

    public PointHistoryEntryDto toPointEntryDto(UserScoreDocument document) {
        if (document == null) return null;

        return PointHistoryEntryDto.builder()
                .createdAt(document.getCreatedAt() != null ? document.getCreatedAt().toString() : "")
                .points(document.getPointsEarned() != null ? document.getPointsEarned() : 0)
                .build();
    }
}
