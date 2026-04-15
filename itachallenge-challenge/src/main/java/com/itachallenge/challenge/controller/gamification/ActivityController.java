package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.ActivityPointsRequest;
import com.itachallenge.challenge.dto.gamification.ActivityPointsResponse;
import com.itachallenge.gamification.service.UserScoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final UserScoreService userScoreService;

    @PostMapping("/points")
    public Mono<ResponseEntity<ActivityPointsResponse>> assignPoints(
            @RequestBody @Valid ActivityPointsRequest request
    ) {

        return userScoreService.assignPoints(
                request.getUserId(),
                request.getActivityType()
        ).map(pointsEarned ->
                ResponseEntity.ok(
                        new ActivityPointsResponse(
                                pointsEarned,
                                request.getActivityType()
                        )
                )
        );
    }
}