package com.itachallenge.score.service;

import com.itachallenge.score.dto.zmq.TestingValuesResponseDto;
import com.itachallenge.score.dto.zmq.ChallengeRequestDto;
import com.itachallenge.score.mqclient.ZMQClient;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ScoreServiceImpl implements IScoreService{

    private static final Pattern UUID_FORM = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", Pattern.CASE_INSENSITIVE);

    @Autowired
    ZMQClient zmqClient;

    //TODO Proof of concept. Missing proper validations
    public Mono<TestingValuesResponseDto> getTestParams(String challengeId) {
        return validateUUID(challengeId)
                .flatMap(validUUID -> {
                    ChallengeRequestDto challengeRequestDto = ChallengeRequestDto.builder()
                            .challengeId(validUUID)
                            .build();
                    return Mono.fromFuture(zmqClient
                            .sendMessage(challengeRequestDto, TestingValuesResponseDto.class)
                            .thenApply(TestingValuesResponseDto.class::cast));
                });
    }

    private Mono<UUID> validateUUID(String id) {
        boolean validUUID = !StringUtils.isEmpty(id) && UUID_FORM.matcher(id).matches();

        if (!validUUID) {
            log.warn("Invalid ID format: {}", id);
            return Mono.error(new IllegalArgumentException("Invalid ID format. Please indicate the correct format."));
        }

        return Mono.just(UUID.fromString(id));
    }
}
