package com.itachallenge.challenge.dto.gamification;

import com.itachallenge.gamification.enums.ActivityType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityPointsRequest {

    @NotNull(message = "userId must not be null")
    private UUID userId;

    @NotNull(message = "activityType must not be null")
    private ActivityType activityType;
}
