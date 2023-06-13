package com.itachallenge.challenge.custom.repositoriesC;

import com.itachallenge.challenge.custom.documentsC.ChallengeDocWithDBRefC;
import com.itachallenge.challenge.custom.documentsC.ChallengeDocWithoutDBRefC;
import com.itachallenge.challenge.custom.documentsC.LanguageDocC;
import org.junit.jupiter.api.*;
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

@DataMongoTest
//@ExtendWith(SpringExtension.class)
//@SpringBootTest

//@PropertySource("classpath:persistence-test.properties")
@ActiveProfiles("test")
public class ChallengeWithoutDBRefRepositoryCLocalTest {

    @Autowired
    private ChallengeWithoutDBRefRepositoryC challengeRepo;

    @Autowired
    private LanguageRepositoryC languageRepo; //required for testing when DBRef is implied (mongo not allows cascade)

    @Autowired
    private Environment environment;

    private final UUID challengeId = UUID.randomUUID();

    private Set<LanguageDocC> languages;

    private Set<Integer> languagesIds;

    //@Test
    void xxxxx(){
        languageRepo.deleteAll().block();
        challengeRepo.deleteAll().block();
    }

    @BeforeEach
    void languagesInit(){
        Integer languageId1 = getInexistentLanguageId(null);
        LanguageDocC languageDoc1 = new LanguageDocC(languageId1, "someName");
        Integer languageId2 = getInexistentLanguageId(languageId1);
        LanguageDocC languageDoc2 = new LanguageDocC(languageId2, "otherName");
        languages = Set.of(languageDoc1, languageDoc2);
        languagesIds = Set.of(languageId1, languageId2);
        languageRepo.saveAll(languages).blockLast();
    }

    @AfterEach
    void languagesEnd(){
        languageRepo.deleteAll(languages).block();
    }

    @Test
    void demoInit(){
        Assertions.assertNotNull(environment);
        System.out.println(environment.getProperty("spring.data.mongodb.uri"));
    }

    @Test
    @DisplayName("Save one challenge document test")
    void saveOneTest(){
        UUID id = getInexistentChallengeId();
        ChallengeDocWithoutDBRefC challengeDoc = new ChallengeDocWithoutDBRefC(id, "some title", "some level",
                languagesIds, LocalDateTime.now());

        Long previousCount = challengeRepo.count().block();

        ChallengeDocWithoutDBRefC challengePersisted = challengeRepo.save(challengeDoc).block();
        assertEquals(previousCount+1, challengeRepo.count().block());
        assertThat(challengePersisted).isEqualTo(challengeDoc);

        challengeRepo.deleteById(id).block();
    }

    //If not DBRef this test it's not needed
    //@Test
    //@DisplayName("Save one challenge without languages previously persisted")

    @Test
    @DisplayName("Try to duplicate ID test")
    void exceptionWhenDuplicatedIdTest(){
        UUID id = getInexistentChallengeId();
        ChallengeDocWithoutDBRefC challengeDoc = new ChallengeDocWithoutDBRefC(id, null, null,
                null, null);
        ChallengeDocWithoutDBRefC challengeDocSameId = new ChallengeDocWithoutDBRefC(id, null, null,
                null, null);

        challengeRepo.save(challengeDoc).block();
        assertThrows(DuplicateKeyException.class,()-> challengeRepo.insert(challengeDocSameId).block());

        challengeRepo.deleteById(id).block();
    }
    @Test
    @DisplayName("Find by ID test")
    void findByIdTest(){
        UUID challengeId = getInexistentChallengeId();
        ChallengeDocWithoutDBRefC challengeDoc = new ChallengeDocWithoutDBRefC(challengeId, "some title", "some level",
                languagesIds, null);

        challengeRepo.save(challengeDoc).block();

        assertTrue(challengeRepo.existsById(challengeId).block());

        Mono<ChallengeDocWithoutDBRefC> challengeFoundPublisher = challengeRepo.findById(challengeId);

        StepVerifier.create(challengeFoundPublisher)
                .assertNext(challengeFound ->
                        assertThat(challengeFound).usingRecursiveComparison().isEqualTo(challengeDoc))
                .verifyComplete();

        challengeRepo.deleteById(challengeId).block();
    }

    @Test
    @DisplayName("Delete by Id test")
    void deleteByIdTest(){
        UUID id = getInexistentChallengeId();
        ChallengeDocWithoutDBRefC challengeDoc = new ChallengeDocWithoutDBRefC(challengeId, "some title", "some level",
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
