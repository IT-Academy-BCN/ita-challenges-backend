package com.itachallenge.challenge.dto.gamification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointEntryDto {
    @JsonProperty("date")
    private String createdAt;
    private int points;
}
