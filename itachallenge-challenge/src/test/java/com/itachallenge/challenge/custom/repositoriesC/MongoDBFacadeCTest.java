package com.itachallenge.challenge.custom.repositoriesC;

import com.itachallenge.challenge.custom.documentsC.ChallengeDocC;
import com.itachallenge.challenge.custom.documentsC.LanguageDocC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@DataMongoTest
@ExtendWith(SpringExtension.class)
@SpringBootTest

//@PropertySource("classpath:persistence-test.properties")
@ActiveProfiles("test")
public class MongoDBFacadeCTest {

    @Autowired
    private MongoDBFacadeC mongoDB;


    //for init DB. TODO: remove + replace logic once local DB is populated with script
    @Autowired
    private ChallengeRepositoryC challengeRepo;

    @Autowired
    private LanguageRepositoryC languageRepo;

    private Set<Integer> languagesIds;

    private UUID challengeId;

    private ChallengeDocC challengeDoc;

    @BeforeEach
    void populate(){
        Integer languageId1 = getInexistentLanguageId(null);
        LanguageDocC languageDoc1 = new LanguageDocC(languageId1, "someName");
        Integer languageId2 = getInexistentLanguageId(languageId1);
        LanguageDocC languageDoc2 = new LanguageDocC(languageId2, "otherName");
        languagesIds = Set.of(languageId1, languageId2);

        challengeId = getInexistentChallengeId();

        challengeDoc = new ChallengeDocC(challengeId, "some title", "some level",
                languagesIds,null);
        challengeDoc.setLanguages(Set.of(languageDoc1, languageDoc2));

        languageRepo.saveAll(Set.of(languageDoc1, languageDoc2)).blockLast();
        challengeRepo.save(challengeDoc).block();
    }

    @AfterEach
    //@Test
    void restore(){
        languageRepo.deleteAllById(languagesIds).block();
        challengeRepo.deleteById(challengeId).block();
    }

    @Test
    @DisplayName("Find challenge by Id populating challenge with the languages test")
    void findOneChallengeTest(){
        Mono<ChallengeDocC> challengePublisher = mongoDB.findOneChallenge(challengeId);

        StepVerifier.create(challengePublisher)
                .assertNext(challengeFound -> {
                    assertThat(challengeFound).usingRecursiveComparison().isEqualTo(challengeDoc);
                    //System.out.println(challengeFound);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Find all challenges test")
    void findAllChallengesTest(){
        Flux<ChallengeDocC> challengesPublisher = mongoDB.findAllChallenges();

        StepVerifier.create(challengesPublisher)
                .assertNext(challenge -> {
                    assertThat(challenge).usingRecursiveComparison().isEqualTo(challengeDoc);
                    //System.out.println(challenge);
                })
                .verifyComplete();
    }


    @Test
    @DisplayName("Find all languages by ids test")
    void findAllLanguagesByIdTest(){
        Flux<LanguageDocC> languagesPublisher = mongoDB.findAllLanguagesById(languagesIds);

        StepVerifier.create(languagesPublisher)
                .assertNext(languageDocC -> assertTrue(languagesIds.contains(languageDocC.getLanguageId())))
                .assertNext(languageDocC -> assertTrue(languagesIds.contains(languageDocC.getLanguageId())))
                .verifyComplete();
    }

    private UUID getInexistentChallengeId(){
        UUID id = UUID.randomUUID();
        while (challengeRepo.existsById(id).block()){
            id = UUID.randomUUID();
        }
        return id;
    }

    private Integer getInexistentLanguageId(Integer discartedId){
        int increment = discartedId != null ? 1 : 0;
        Integer id = 1000+increment;
        while (languageRepo.existsById(id).block()){
            id += 1;
        }
        return id;
    }
}
