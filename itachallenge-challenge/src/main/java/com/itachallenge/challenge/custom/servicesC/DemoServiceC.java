package com.itachallenge.challenge.custom.servicesC;

import com.itachallenge.challenge.custom.documentsC.ChallengeDocC;
import com.itachallenge.challenge.custom.documentsC.LanguageDocC;
import com.itachallenge.challenge.custom.repositoriesC.ChallengeRepositoryC;
import com.itachallenge.challenge.custom.repositoriesC.LanguageRepositoryC;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DemoServiceC {

    private final ChallengeRepositoryC challengeRepo;

    private final LanguageRepositoryC languageRepo;

    Logger log = LoggerFactory.getLogger(DemoServiceC.class);

    public void saveOneChallengeWithDBRefEnabled(){
        log.info("---------saveOneChallengeWithDBRefEnabled");

        Integer languageId = 1000;
        Set<LanguageDocC> languages = Set.of(new LanguageDocC(languageId, "any"));

        UUID challengeId = UUID.randomUUID();
        ChallengeDocC challenge = new ChallengeDocC(
                challengeId,null, null, languages, null);

        challengeRepo.save(challenge).block();
        log.info("Challenge persisted. But language has not been persisted previously");

        ChallengeDocC challengeFound = challengeRepo.findById(challengeId).block();
        log.info("When loaded the languages are not loaded, because mongo does not allow CASCADE.");
        log.info("Are all languages loaded in challenge null?: "+
                challengeFound.getLanguages().stream().allMatch(language -> language == null));

        log.info("Lets persist the lanugages......");
        languageRepo.saveAll(languages).blockLast();
        log.info("Now the language is persisted?: "+languageRepo.existsById(languageId).block());

        log.info("Lets try again...");
        challengeFound = challengeRepo.findById(challengeId).block();
        log.info("Now, when the lookup is requested, the language is found...");
        log.info("Num of languages loaded with the challenge: "+challengeFound.getLanguages().size());
        log.info("Let's see the languages loaded with the challenge: "+challengeFound.getLanguages().toString());

        languageRepo.deleteAll(languages).block();
        challengeRepo.deleteById(challengeId).block();

    }

    public void saveOneChallengeWithoutNoReactiveDependency(){
        log.info("---------saveOneChallengeWithoutNoReactiveDependency");

        Integer languageId = 1000;
        Set<LanguageDocC> languages = Set.of(new LanguageDocC(languageId, "any"));

        UUID challengeId = UUID.randomUUID();
        ChallengeDocC challenge = new ChallengeDocC(
                challengeId,null, null, languages, null);

        challengeRepo.save(challenge).block();
        log.info("Challenge persisted. Does not matter if language has been persisted previously or not...");

        try {
            ChallengeDocC challengeFound = challengeRepo.findById(challengeId).block();
        }catch (UnsupportedOperationException ex){
            log.error(ex.getMessage());
            log.info("challenge can't be loaded if we use DBRef with only reactive mongo dependency...");
            ex.printStackTrace();
        }

        languageRepo.deleteAll(languages).block();
        challengeRepo.deleteById(challengeId).block();

        log.info("What if when challenge persisted with empty/null languages...");

        challengeId = UUID.randomUUID();
        challenge = new ChallengeDocC(
                challengeId,null, null, Set.of(), null);

        challengeRepo.save(challenge).block();

        try {
            challengeRepo.findById(challengeId).block();
            log.info("No exception is thrown...due there's nothing to include...");
        }catch (UnsupportedOperationException ex){
            log.error(ex.getMessage());
            log.info("challenge can't be loaded if we use DBRef with only reactive mongo dependency...");
            ex.printStackTrace();
        }

        challengeRepo.deleteById(challengeId).block();
    }

}
