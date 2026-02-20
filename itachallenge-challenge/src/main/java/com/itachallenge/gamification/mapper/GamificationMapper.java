package com.itachallenge.gamification.mapper;

import com.itachallenge.challenge.dto.gamification.PointEntryDto;
import com.itachallenge.gamification.document.UserScoreDocument;
import org.springframework.stereotype.Component;

@Component
public class GamificationMapper {

    public PointEntryDto toPointEntryDto(UserScoreDocument document) {
        if (document == null) return null;

        return PointEntryDto.builder()
                .createdAt(document.getCreatedAt() != null ? document.getCreatedAt().toString() : "")
                .points(document.getPoints() != null ? document.getPoints() : 0)
                .build();
    }
}
