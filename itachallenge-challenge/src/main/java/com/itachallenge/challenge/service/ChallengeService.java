package com.itachallenge.challenge.service;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeRepository challengeRepository;

    public boolean removeResourcesById(UUID idResource){
        Flux<Challenge> challengeFlux = challengeRepository.findAllByResourcesContaining(idResource);

        return challengeFlux.flatMap(challenge -> {
                    challenge.setResources(challenge.getResources().stream()
                            .filter(s -> !s.equals(idResource))
                            .collect(Collectors.toSet()));
                    return challengeRepository.save(challenge);
                })
                .hasElements()
                .blockOptional()
                .orElse(false);
    }

}
