package com.itachallenge.challenge.service;

import com.itachallenge.challenge.documents.Challenge;
import com.itachallenge.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeService {
    private final ChallengeRepository challengeRepository;

    public Flux<Challenge> getAll(){return challengeRepository.findAll();}

    public Flux<Challenge> getByResource(String idResource){return challengeRepository.findAllByResourcesContaining(idResource);}

    public boolean removeResourcesById(String idResource){
        Flux<Challenge> challengeFlux = challengeRepository.findAllByResourcesContaining(idResource);

        return Boolean.TRUE.equals(challengeFlux.flatMap(challenge -> {
                    challenge.setResources(challenge.getResources().stream()
                            .filter(s -> !s.equals(idResource))
                            .collect(Collectors.toSet()));

                    return challengeRepository.save(challenge);
                })
                .hasElements()
                .block());
    }
}
