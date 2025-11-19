package com.itachallenge.user.dto.userinteraction.bookmark;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkResponseDto {

    @JsonProperty(value = "uuid_favorite")
    private UUID uuid;

    @JsonProperty(value = "user_id")
    private UUID userId;

    @JsonProperty(value = "challenge_id")
    private UUID challengeId;

    @JsonProperty(value = "created_at")
    private LocalDateTime createdAt;
}
