package com.itachallenge.challenge.dto.gamification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityTypeResponseDto {
    private List<String> activityTypes;
}
