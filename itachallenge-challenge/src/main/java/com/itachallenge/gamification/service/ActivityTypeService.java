package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.ActivityTypeResponseDto;
import reactor.core.publisher.Mono;

public interface ActivityTypeService {

    Mono<ActivityTypeResponseDto> getAvailableActivityTypes();
}
