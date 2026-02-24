package com.itachallenge.challenge.dto.gamification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PointEntryDto {
    @JsonProperty("date")
    private String createdAt;
    private int points;
}
