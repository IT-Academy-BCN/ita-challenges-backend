package com.itachallenge.challenge.controller.gamification;

import com.itachallenge.challenge.dto.gamification.RankingResponseDto;
import com.itachallenge.gamification.service.PointsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/itachallenge/api/v1/users/ranking")
public class RankingController {

    private final PointsService pointsService;

    public RankingController(PointsService pointsService) {
        this.pointsService = pointsService;
    }

    @GetMapping
    public Flux<RankingResponseDto> getRanking(){
        return pointsService.getRankingAscOrder();
    }


}
