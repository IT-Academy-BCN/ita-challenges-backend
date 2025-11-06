package com.itachallenge.user.dto.userinteraction.favorite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteResponseDto {
    private UUID id;
    private UUID userId;
    private UUID challengeId;
    private LocalDateTime createdAt;
}
