package com.itachallenge.challenge.custom.repositoriesC;

import com.itachallenge.challenge.custom.documentsC.ChallengeDocC;
import com.itachallenge.challenge.custom.documentsC.LanguageDocC;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MongoDBFacadeC {

    private final ChallengeRepositoryC challengeRepo;

    private final LanguageRepositoryC languageRepo;

    public Flux<ChallengeDocC> findAllChallenges(){
        return challengeRepo.findAll()
                .flatMap(challenge ->
                            languageRepo.findAllById(challenge.getLanguagesIds())
                            .collect(Collectors.toSet())
                            .map(languagesSets -> {
                                challenge.setLanguages(languagesSets);
                                return challenge;})
                );
    }

    public Mono<ChallengeDocC> findOneChallenge(UUID challengeId){
        return challengeRepo.findById(challengeId)
                .flatMap(challenge ->
                    languageRepo.findAllById(challenge.getLanguagesIds())
                            .collect(Collectors.toSet())
                            .map(languagesSets -> {
                                challenge.setLanguages(languagesSets);
                                return challenge;
                            })
                );
    }

    public Flux<LanguageDocC> findAllLanguagesById(Iterable<Integer> lanugagesId){
        return languageRepo.findAllById(lanugagesId);
    }
}
