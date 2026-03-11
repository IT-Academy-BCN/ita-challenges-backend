package com.itachallenge.challenge.dto.gamification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class LeaderboardResponseDto {
    private List<LeaderboardEntryDto> leaderboard;

    @JsonProperty("page")
    private int currentPage;

    @JsonProperty("size")
    private int pageSize;

    private long totalElements;
    private int totalPages;
}
