package com.itachallenge.challenge.custom.repositoriesC;

import com.itachallenge.challenge.custom.documentsC.ChallengeDocWithDBRefC;
import com.itachallenge.challenge.custom.documentsC.LanguageDocC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
//@ExtendWith(SpringExtension.class)
//@SpringBootTest

//@PropertySource("classpath:persistence-test.properties")
@ActiveProfiles("test")
public class ChallengeWithDBRefRepositoryCLocalTest {

    @Autowired
    private ChallengeWithDBRefRepositoryC challengeRepo;

    @Autowired
    private LanguageRepositoryC languageRepo; //required for testing when DBRef is implied (mongo not allows cascade)

    @Autowired
    private Environment environment;

    private final UUID challengeId = UUID.randomUUID();

    //@Test
    void xxxxx(){
        languageRepo.deleteAll().block();
        challengeRepo.deleteAll().block();
    }

    @Test
    void demoInit(){
        Assertions.assertNotNull(environment);
        System.out.println(environment.getProperty("spring.data.mongodb.uri"));
    }

    @Test
    @DisplayName("Save one challenge document test")
    void saveOneTest(){
        Integer languageId1 = getInexistentLanguageId(null);
        LanguageDocC languageDoc1 = new LanguageDocC(languageId1, "someName");
        LanguageDocC languageDoc2 = new LanguageDocC(getInexistentLanguageId(languageId1), "otherName");

        Set<LanguageDocC> languages = Set.of(languageDoc1, languageDoc2);
        UUID id = getInexistentChallengeId();
        ChallengeDocWithDBRefC challengeDoc = new ChallengeDocWithDBRefC(id, "some title", "some level",
                languages, LocalDateTime.now());

        Long previousCount = challengeRepo.count().block();

        languageRepo.saveAll(languages).blockLast();

        ChallengeDocWithDBRefC challengePersisted = challengeRepo.save(challengeDoc).block();
        assertEquals(previousCount+1, challengeRepo.count().block());
        assertThat(challengePersisted).isEqualTo(challengeDoc);

        challengeRepo.deleteById(id).block();
        languageRepo.deleteAll(languages).block();
    }

    @Test
    @DisplayName("Save one challenge without languages previously persisted")
    void saveOneWithoutLanguagesPersistedTest(){
        Integer languageId1 = getInexistentLanguageId(null);
        LanguageDocC languageDoc1 = new LanguageDocC(languageId1, "someName");
        LanguageDocC languageDoc2 = new LanguageDocC(getInexistentLanguageId(languageId1), "otherName");

        Set<LanguageDocC> languages = Set.of(languageDoc1, languageDoc2);
        UUID id = getInexistentChallengeId();
        ChallengeDocWithDBRefC challengeDoc = new ChallengeDocWithDBRefC(id, "some title", "some level",
                languages, LocalDateTime.now());

        challengeRepo.save(challengeDoc).block();

        Mono<ChallengeDocWithDBRefC> challengeDocFound = challengeRepo.findById(id);
        StepVerifier.create(challengeDocFound)
                .assertNext(challenge -> assertTrue(challenge.getLanguages().isEmpty()))
                .verifyComplete();

        challengeRepo.deleteById(id).block();
    }

    @Test
    @DisplayName("Try to duplicate ID test")
    void exceptionWhenDuplicatedIdTest(){
        UUID id = getInexistentChallengeId();
        ChallengeDocWithDBRefC challengeDoc = new ChallengeDocWithDBRefC(id, "some title", "some level",
                null, LocalDateTime.now());
        ChallengeDocWithDBRefC challengeDocSameId = new ChallengeDocWithDBRefC(id, "some title2", "some level2",
                null, LocalDateTime.now());

        challengeRepo.save(challengeDoc).block();
        assertThrows(DuplicateKeyException.class,()-> challengeRepo.insert(challengeDocSameId).block());

        challengeRepo.deleteById(id).block();
    }

    @Test
    @DisplayName("Find by ID test")
    void findByIdTest(){
        Integer languageId = getInexistentLanguageId(null);
        LanguageDocC languageDoc = new LanguageDocC(languageId, "someName");

        UUID challengeId = getInexistentChallengeId();
        ChallengeDocWithDBRefC challengeDoc = new ChallengeDocWithDBRefC(challengeId, "some title", "some level",
                Set.of(languageDoc), null);

        languageRepo.save(languageDoc).block();
        challengeRepo.save(challengeDoc).block();

        assertTrue(challengeRepo.existsById(challengeId).block());

        Mono<ChallengeDocWithDBRefC> challengeFoundPublisher = challengeRepo.findById(challengeId);

        StepVerifier.create(challengeFoundPublisher)
                .assertNext(challengeFound ->
                        assertThat(challengeFound).usingRecursiveComparison().isEqualTo(challengeDoc))
                .verifyComplete();

        languageRepo.deleteById(languageId).block();
        challengeRepo.deleteById(challengeId).block();
    }

    @Test
    @DisplayName("Delete by Id test")
    void deleteByIdTest(){
        UUID id = getInexistentChallengeId();
        ChallengeDocWithDBRefC challengeDoc = new ChallengeDocWithDBRefC(challengeId, "some title", "some level",
                null, null);

        Long previousCount = challengeRepo.count().block();
        challengeRepo.save(challengeDoc).block();
        assertTrue(challengeRepo.existsById(id).block());
        assertEquals(previousCount+1, challengeRepo.count().block());

        challengeRepo.deleteById(challengeId).block();
        assertEquals(previousCount, challengeRepo.count().block());
        assertFalse(challengeRepo.existsById(id).block());
    }

    private UUID getInexistentChallengeId(){
        UUID id = challengeId;
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
