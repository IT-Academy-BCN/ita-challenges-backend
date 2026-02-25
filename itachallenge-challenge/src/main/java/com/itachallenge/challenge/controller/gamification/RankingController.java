package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.RankingResponseDto;
import com.itachallenge.gamification.service.UserScoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/itachallenge/api/v1/users/ranking")
public class RankingController {

    private final UserScoreService pointsService;

    public RankingController(UserScoreService pointsService) {
        this.pointsService = pointsService;
    }

    @GetMapping
    public Flux<RankingResponseDto> getRanking(){
        return pointsService.getRankingDescOrder();
    }
}
