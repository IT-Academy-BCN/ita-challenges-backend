package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.RankingResponseDto;
import com.itachallenge.gamification.exception.ServiceException;
import com.itachallenge.gamification.repository.UserScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;


@Service
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {

    private final UserScoreRepository userScoreRepository;
    private static final Logger log = LoggerFactory.getLogger(PointsServiceImpl.class);

    @Override
    public Flux<RankingResponseDto> getRankingDescOrder() {
        return userScoreRepository.findUsersRanking()
                .onErrorResume(DataAccessException.class, ex -> {
                    log.error("DB error fetching ranking: {}", ex.getMessage());
                    return Flux.error(new ServiceException("Could not retrieve ranking"));
                });
    }
}
