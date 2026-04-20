package com.itachallenge.gamification.service;

import com.itachallenge.challenge.dto.gamification.ActivityTypeResponseDto;
import com.itachallenge.gamification.enums.ActivityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ActivityTypeServiceImpl implements ActivityTypeService {

    @Override
    public Mono<ActivityTypeResponseDto> getAvailableActivityTypes() {
        return Mono.just(ActivityTypeResponseDto.builder()
                .activityTypes(Arrays.stream(ActivityType.values())
                        .map(Enum::name)
                        .toList())
                .build());
    }
}
